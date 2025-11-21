package com.eventaro.Eventaro.persistence;

import com.eventaro.Eventaro.domain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    // Finde Rechnung zu einer bestimmten Buchung
    Optional<Invoice> findByBookingId(Integer bookingId);
}