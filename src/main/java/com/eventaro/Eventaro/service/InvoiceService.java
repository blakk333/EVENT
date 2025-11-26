package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.*;
import com.eventaro.Eventaro.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private int invoiceCounter = 1000;

    @Transactional
    public Invoice createInvoiceFromBooking(Booking booking) {
        List<Invoice> existing = invoiceRepository.findByBookingRef(booking.getBookingNumber());
        if (!existing.isEmpty()) return existing.get(0);

        Invoice inv = new Invoice(booking.getBookingNumber(), booking.getGuestName(), "Musterstraße 1, 1010 Wien");

        // --- NEU: Anzahlung übernehmen ---
        inv.setAdvancePayment(booking.getDepositAmount());

        double price = booking.getNegotiatedPricePerPerson() != null ?
                booking.getNegotiatedPricePerPerson() : booking.getStandardPricePerPerson();

        inv.addItem(new InvoiceItem("Teilnahme: " + booking.getEventName(), booking.getParticipantCount(), price, 0.20));

        // Demo-Items
        inv.addItem(new InvoiceItem("Ausrüstungsverleih (Helm & Gurt)", 2, 15.00, 0.20));
        inv.addItem(new InvoiceItem("Getränkepauschale", 5, 4.50, 0.10));

        return invoiceRepository.save(inv);
    }

    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    // Veraltete Methode (kann bleiben für Kompatibilität, ruft aber die neue auf)
    @Transactional
    public void finalizeInvoice(String invoiceId) {
        finalizeInvoice(invoiceId, PaymentMethod.CASH); // Default
    }

    // --- NEU: Finalize mit PaymentMethod ---
    @Transactional
    public void finalizeInvoice(String invoiceId, PaymentMethod method) {
        Invoice inv = getInvoiceById(invoiceId);
        if (inv != null && inv.getStatus() == InvoiceStatus.DRAFT) {
            inv.setStatus(InvoiceStatus.FINAL);
            inv.setPaymentMethod(method); // Setze Methode
            inv.setInvoiceNumber("RE-" + LocalDate.now().getYear() + "-" + (++invoiceCounter));
            invoiceRepository.save(inv);
        }
    }

    @Transactional
    public Invoice splitItemToNewInvoice(String originalInvoiceId, String itemId) {
        Invoice origin = getInvoiceById(originalInvoiceId);
        if (origin == null) return null;

        InvoiceItem itemToMove = origin.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElse(null);

        if (itemToMove == null) return origin;

        Invoice splitInvoice = findOrCreateSplitInvoice(origin);

        origin.removeItem(itemToMove);
        splitInvoice.addItem(itemToMove);

        invoiceRepository.save(origin);
        invoiceRepository.save(splitInvoice);

        return origin;
    }

    private Invoice findOrCreateSplitInvoice(Invoice origin) {
        return invoiceRepository.findByBookingRef(origin.getBookingRef()).stream()
                .filter(i -> !i.getId().equals(origin.getId()) && i.getStatus() == InvoiceStatus.DRAFT)
                .findFirst()
                .orElseGet(() -> {
                    Invoice newInv = new Invoice(origin.getBookingRef(), "Split Empfänger (bitte ändern)", "");
                    return invoiceRepository.save(newInv);
                });
    }

    public List<Invoice> getInvoicesForBooking(String bookingRef) {
        return invoiceRepository.findByBookingRef(bookingRef);
    }

    public double calculateRevenueToday() {
        return 0.0; // Dummy für Dashboard
    }
}