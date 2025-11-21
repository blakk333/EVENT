package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.domain.model.*;
import com.eventaro.Eventaro.persistence.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final AuditLogService auditLogService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          BookingRepository bookingRepository,
                          AuditLogService auditLogService) {
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Invoice createInvoiceFromBooking(Integer bookingId) {
        // 1. Prüfen, ob schon eine Rechnung existiert
        Optional<Invoice> existing = invoiceRepository.findByBookingId(bookingId);
        if (existing.isPresent()) {
            return existing.get(); // Rückgabe der existierenden Rechnung
        }

        // 2. Buchung laden
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // 3. Rechnungskopf erstellen
        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setPaymentMethod(booking.getPaymentMethod());

        // Kundendaten festschreiben (Snapshot)
        Customer cust = booking.getCustomer();
        invoice.setRecipientName(cust.getFirstName() + " " + cust.getLastName());

        if (cust.getAddress() != null) {
            String addrStr = cust.getAddress().getStreet() + " " +
                    cust.getAddress().getHousenumber() + ", " +
                    cust.getAddress().getZipCode() + " " +
                    cust.getAddress().getCity();
            invoice.setRecipientAddress(addrStr);
        }

        // Rechnungsnummer generieren (Format: INV-JAHR-ZUFALL)
        String invNum = "INV-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0,6).toUpperCase();
        invoice.setInvoiceNumber(invNum);

        // 4. Positionen hinzufügen
        // Position 1: Tickets
        double pricePerTicket = booking.getEvent().getBasePrice();
        InvoiceItem ticketItem = new InvoiceItem(
                "Event Ticket: " + booking.getEvent().getName(),
                booking.getTicketCount(),
                pricePerTicket
        );
        invoice.addItem(ticketItem);

        // (Hier könnten später noch Additional Services hinzugefügt werden)

        // 5. Speichern
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 6. Logging
        auditLogService.log("CREATE_INVOICE", "Generated Invoice " + savedInvoice.getInvoiceNumber() + " for Booking " + booking.getBookingNumber());

        return savedInvoice;
    }

    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
    }
}