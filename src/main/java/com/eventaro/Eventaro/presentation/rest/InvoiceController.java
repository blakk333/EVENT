package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.booking.BookingService;
import com.eventaro.Eventaro.application.invoice.InvoiceService;
import com.eventaro.Eventaro.application.invoice.dto.InvoiceResponseDTO;
import com.eventaro.Eventaro.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/view/{id}")
    public String viewInvoice(@PathVariable String id, Model model) {
        // Service liefert jetzt DTO
        InvoiceResponseDTO invoice = invoiceService.getInvoiceById(id);
        if (invoice == null) return "redirect:/bookings/today";

        model.addAttribute("invoice", invoice);
        model.addAttribute("relatedInvoices", invoiceService.getInvoicesForBooking(invoice.getBookingRef()));

        // Das Booking benötigen wir für das Feedback-Formular auf der Rechnung
        // Hinweis: bookingService.findByNumber gibt aktuell noch die Entity zurück (siehe vorheriger Schritt)
        // Falls Sie das BookingDTO verwenden, müssen Sie im Template prüfen, ob die Felder "rating" etc. vorhanden sind.
        bookingService.findByNumber(invoice.getBookingRef())
                .ifPresent(booking -> model.addAttribute("linkedBooking", booking));

        return "management/invoice-view";
    }

    @PostMapping("/split")
    public String splitInvoiceItem(@RequestParam String invoiceId, @RequestParam String itemId) {
        invoiceService.splitItemToNewInvoice(invoiceId, itemId);
        return "redirect:/invoices/view/" + invoiceId;
    }

    @PostMapping("/finalize")
    public String finalizeInvoice(@RequestParam String invoiceId,
                                  @RequestParam PaymentMethod paymentMethod) {
        invoiceService.finalizeInvoice(invoiceId, paymentMethod);
        return "redirect:/invoices/view/" + invoiceId;
    }
}