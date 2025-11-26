package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.model.Invoice;
import com.eventaro.Eventaro.model.PaymentMethod;
import com.eventaro.Eventaro.service.BookingService;
import com.eventaro.Eventaro.service.InvoiceService;
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
        Invoice invoice = invoiceService.getInvoiceById(id);
        if (invoice == null) return "redirect:/bookings/today";

        model.addAttribute("invoice", invoice);
        model.addAttribute("relatedInvoices", invoiceService.getInvoicesForBooking(invoice.getBookingRef()));

        bookingService.findByNumber(invoice.getBookingRef())
                .ifPresent(booking -> model.addAttribute("linkedBooking", booking));

        return "management/invoice-view";
    }

    @PostMapping("/split")
    public String splitInvoiceItem(@RequestParam String invoiceId, @RequestParam String itemId) {
        invoiceService.splitItemToNewInvoice(invoiceId, itemId);
        return "redirect:/invoices/view/" + invoiceId;
    }

    // --- NEU: Finalisieren mit Zahlungsmethode ---
    @PostMapping("/finalize")
    public String finalizeInvoice(@RequestParam String invoiceId,
                                  @RequestParam PaymentMethod paymentMethod) {
        invoiceService.finalizeInvoice(invoiceId, paymentMethod);
        return "redirect:/invoices/view/" + invoiceId;
    }
}