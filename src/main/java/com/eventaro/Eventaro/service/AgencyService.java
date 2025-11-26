package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.Contingent;
import com.eventaro.Eventaro.model.CorporateClient;
import com.eventaro.Eventaro.model.Event;
import com.eventaro.Eventaro.repository.ContingentRepository;
import com.eventaro.Eventaro.repository.CorporateClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AgencyService {

    @Autowired
    private CorporateClientRepository clientRepo;

    @Autowired
    private ContingentRepository contingentRepo;

    // --- Corporate Client Management ---

    public List<CorporateClient> getAllClients() {
        return clientRepo.findAll();
    }

    public Optional<CorporateClient> getClientById(Long id) {
        return clientRepo.findById(id);
    }

    /**
     * Findet einen Partner anhand seines geheimen Buchungscodes.
     * Wichtig für das öffentliche Buchungsformular (WI #54).
     */
    public Optional<CorporateClient> getClientByBookingCode(String bookingCode) {
        // Da wir keine explizite Methode im Repo definiert haben, filtern wir hier im Stream.
        // In einer echten App würde man 'findByBookingCode' im Repository ergänzen.
        return clientRepo.findAll().stream()
                .filter(c -> bookingCode != null && bookingCode.equalsIgnoreCase(c.getBookingCode()))
                .findFirst();
    }

    @Transactional
    public CorporateClient saveClient(CorporateClient client) {
        // WI #54 / Anpassung: Automatisch einen Booking-Code generieren, falls keiner da ist
        if (client.getBookingCode() == null || client.getBookingCode().trim().isEmpty()) {
            String prefix = client.getName().length() >= 3
                    ? client.getName().substring(0, 3).toUpperCase()
                    : "PRT";
            // Entfernt Leerzeichen und Sonderzeichen grob
            prefix = prefix.replaceAll("[^a-zA-Z0-9]", "");

            // Code Format: NAME-1234 (z.B. SAP-4829)
            String code = prefix + "-" + (1000 + new Random().nextInt(9000));
            client.setBookingCode(code);
        }

        return clientRepo.save(client);
    }

    @Transactional
    public void deleteClient(Long id) {
        clientRepo.deleteById(id);
    }

    // --- Contingent Management (WI #82) ---

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

    // --- Preisberechnung (WI #81) ---

    /**
     * Berechnet den Preis für ein Event unter Berücksichtigung des Partner-Rabatts.
     */
    public double calculateDiscountedPrice(Event event, Long clientId) {
        return clientRepo.findById(clientId).map(client -> {
            double discount = client.getNegotiatedDiscount() != null ? client.getNegotiatedDiscount() : 0.0;
            // Basispreis - (Basispreis * Rabatt)
            return event.getPrice() * (1.0 - discount);
        }).orElse(event.getPrice());
    }
}