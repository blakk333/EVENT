package com.eventaro.Eventaro.application.invoice;

import com.eventaro.Eventaro.application.invoice.dto.InvoiceResponseDTO;
import com.eventaro.Eventaro.application.invoice.mapper.InvoiceMapper;
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.domain.invoice.*; // Entities, Status, PaymentMethod
import com.eventaro.Eventaro.enums.InvoiceStatus;
import com.eventaro.Eventaro.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceMapper invoiceMapper;

    private int invoiceCounter = 1000;

    // Gibt DTO zurück
    @Transactional
    public InvoiceResponseDTO createInvoiceFromBooking(Booking booking) {
        // Prüfen ob schon eine existiert
        List<Invoice> existing = invoiceRepository.findByBookingRef(booking.getBookingNumber());
        if (!existing.isEmpty()) {
            return invoiceMapper.toResponse(existing.get(0));
        }

        Invoice inv = new Invoice(booking.getBookingNumber(), booking.getGuestName(), "Musterstraße 1, 1010 Wien");
        inv.setAdvancePayment(booking.getDepositAmount());

        double price = booking.getNegotiatedPricePerPerson() != null ?
                booking.getNegotiatedPricePerPerson() : booking.getStandardPricePerPerson();

        inv.addItem(new InvoiceItem("Teilnahme: " + booking.getEventName(), booking.getParticipantCount(), price, 0.20));

        // Demo-Items
        inv.addItem(new InvoiceItem("Ausrüstungsverleih (Helm & Gurt)", 2, 15.00, 0.20));
        inv.addItem(new InvoiceItem("Getränkepauschale", 5, 4.50, 0.10));

        Invoice saved = invoiceRepository.save(inv);
        return invoiceMapper.toResponse(saved);
    }

    public InvoiceResponseDTO getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponse)
                .orElse(null);
    }

    // Liefert eine Liste von DTOs
    public List<InvoiceResponseDTO> getInvoicesForBooking(String bookingRef) {
        return invoiceRepository.findByBookingRef(bookingRef).stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void finalizeInvoice(String invoiceId, PaymentMethod method) {
        invoiceRepository.findById(invoiceId).ifPresent(inv -> {
            if (inv.getStatus() == InvoiceStatus.DRAFT) {
                inv.setStatus(InvoiceStatus.FINAL);
                inv.setPaymentMethod(method);
                inv.setInvoiceNumber("RE-" + LocalDate.now().getYear() + "-" + (++invoiceCounter));
                invoiceRepository.save(inv);
            }
        });
    }

    @Transactional
    public InvoiceResponseDTO splitItemToNewInvoice(String originalInvoiceId, String itemId) {
        // Hier müssen wir erst die Entity laden, um zu arbeiten
        Invoice origin = invoiceRepository.findById(originalInvoiceId).orElse(null);
        if (origin == null) return null;

        InvoiceItem itemToMove = origin.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElse(null);

        if (itemToMove == null) return invoiceMapper.toResponse(origin);

        Invoice splitInvoice = findOrCreateSplitInvoice(origin);

        origin.removeItem(itemToMove);
        splitInvoice.addItem(itemToMove);

        invoiceRepository.save(origin);
        invoiceRepository.save(splitInvoice);

        return invoiceMapper.toResponse(origin);
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

    public double calculateRevenueToday() {
        return 0.0;
    }
}