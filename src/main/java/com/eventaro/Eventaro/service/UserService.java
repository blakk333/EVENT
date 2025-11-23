package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.datatransfer.UserRegistrationDto;
import com.eventaro.Eventaro.domain.model.Address;
import com.eventaro.Eventaro.domain.model.Customer;
import com.eventaro.Eventaro.domain.model.User;
import com.eventaro.Eventaro.enums.Country;
import com.eventaro.Eventaro.enums.UserRole;
import com.eventaro.Eventaro.persistence.CustomerRepository;
import com.eventaro.Eventaro.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void registerCustomer(UserRegistrationDto dto) {
        // 1. Prüfen ob User schon existiert
        if (userRepository.findByUsername(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        // 2. User anlegen (Login Daten)
        User user = new User();
        user.setUsername(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        userRepository.save(user);

        // 3. Customer Profil anlegen (Persönliche Daten)
        // Prüfen, ob es evtl. schon einen Gast ohne Login gab mit dieser Email
        Customer customer = customerRepository.findByEmail(dto.getEmail())
                .orElse(new Customer());

        customer.setEmail(dto.getEmail());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());

        // Dummy Adresse initialisieren, falls noch nicht vorhanden (vermeidet NullPointer später)
        if (customer.getAddress() == null) {
            Address address = new Address();
            address.setStreet("");
            address.setCity("");
            address.setHousenumber(0);
            address.setZipCode(1000);
            address.setCountry(Country.AUSTRIA); // Default
            customer.setAddress(address);
        }

        customerRepository.save(customer);

        // Audit Log
        auditLogService.log("REGISTER_USER", "New customer registered: " + dto.getEmail());
    }
}