package com.eventaro.Eventaro.config;

import com.eventaro.Eventaro.domain.model.User;
import com.eventaro.Eventaro.enums.UserRole;
import com.eventaro.Eventaro.persistence.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Passwort verschlüsseln
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("ADMIN USER CREATED: Username 'admin', Password 'admin123'");
            }
        };
    }
}