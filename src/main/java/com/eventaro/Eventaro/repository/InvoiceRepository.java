package com.eventaro.Eventaro.repository;

import com.eventaro.Eventaro.model.Invoice;
import com.eventaro.Eventaro.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    // Finde alle Rechnungen zu einer bestimmten Buchungsnummer
    List<Invoice> findByBookingRef(String bookingRef);

    // Finde eine offene Entwurfs-Rechnung zu einer Buchung (fürs Splitten)
    Optional<Invoice> findByBookingRefAndStatus(String bookingRef, InvoiceStatus status);
}