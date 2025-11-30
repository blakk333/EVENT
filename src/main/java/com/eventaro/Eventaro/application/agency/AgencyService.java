package com.eventaro.Eventaro.application.agency;

import com.eventaro.Eventaro.application.agency.dto.CorporateClientRequest;
import com.eventaro.Eventaro.application.agency.dto.CorporateClientResponse;
import com.eventaro.Eventaro.application.agency.mapper.CorporateClientMapper;
import com.eventaro.Eventaro.domain.agency.Contingent;
import com.eventaro.Eventaro.domain.agency.ContingentRepository;
import com.eventaro.Eventaro.domain.agency.CorporateClient;
import com.eventaro.Eventaro.domain.agency.CorporateClientRepository;
import com.eventaro.Eventaro.domain.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AgencyService {

    @Autowired
    private CorporateClientRepository clientRepo;

    @Autowired
    private ContingentRepository contingentRepo;

    @Autowired
    private CorporateClientMapper clientMapper; // <--- NEU

    // --- Corporate Client Management ---

    // Gibt jetzt DTOs zurück
    public List<CorporateClientResponse> getAllClients() {
        return clientRepo.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<CorporateClientResponse> getClientById(Long id) {
        return clientRepo.findById(id).map(clientMapper::toResponse);
    }

    public Optional<CorporateClientResponse> getClientByBookingCode(String bookingCode) {
        return clientRepo.findAll().stream()
                .filter(c -> bookingCode != null && bookingCode.equalsIgnoreCase(c.getBookingCode()))
                .findFirst()
                .map(clientMapper::toResponse);
    }

    // Nimmt Request entgegen, gibt Response zurück
    @Transactional
    public CorporateClientResponse createOrUpdateClient(CorporateClientRequest request, Long id) {
        CorporateClient client;

        if (id != null) {
            // Update
            client = clientRepo.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));
            clientMapper.updateEntityFromRequest(client, request);
        } else {
            // Create
            client = clientMapper.toEntity(request);
            // Manuelles Mapping für Felder, die nicht im Konstruktor waren (falls nötig)
            client.setContactPerson(request.getContactPerson());
            client.setEmail(request.getEmail());
            client.setAddress(request.getAddress());
        }

        // Generiere Booking Code falls nötig
        ensureBookingCode(client);

        CorporateClient saved = clientRepo.save(client);
        return clientMapper.toResponse(saved);
    }

    private void ensureBookingCode(CorporateClient client) {
        if (client.getBookingCode() == null || client.getBookingCode().trim().isEmpty()) {
            String prefix = client.getName().length() >= 3
                    ? client.getName().substring(0, 3).toUpperCase()
                    : "PRT";
            prefix = prefix.replaceAll("[^a-zA-Z0-9]", "");
            String code = prefix + "-" + (1000 + new Random().nextInt(9000));
            client.setBookingCode(code);
        }
    }

    @Transactional
    public void deleteClient(Long id) {
        clientRepo.deleteById(id);
    }

    // --- Contingent Management ---
    // (Hier könnte man analog ContingentDTOs einführen, aber der Einfachheit halber lassen wir es erstmal so oder nutzen Entities intern)

    @Transactional
    public void reserveContingent(Long clientId, Event event, int count, LocalDate expiry) {
        clientRepo.findById(clientId).ifPresent(client -> {
            Contingent con = new Contingent();
            con.setClient(client);
            con.setEvent(event);
            con.setReservedCount(count);
            con.setExpiryDate(expiry);
            contingentRepo.save(con);
        });
    }

    public List<Contingent> getAllContingents() {
        return contingentRepo.findAll();
    }

    public List<Contingent> getContingentsForEvent(Event event) {
        return contingentRepo.findByEvent(event);
    }

    // --- Preisberechnung ---

    public double calculateDiscountedPrice(Event event, Long clientId) {
        return clientRepo.findById(clientId).map(client -> {
            double discount = client.getNegotiatedDiscount() != null ? client.getNegotiatedDiscount() : 0.0;
            return event.getPrice() * (1.0 - discount);
        }).orElse(event.getPrice());
    }
}