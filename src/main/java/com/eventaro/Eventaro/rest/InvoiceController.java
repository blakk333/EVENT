package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.domain.model.Invoice;
import com.eventaro.Eventaro.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // Aktion: Rechnung generieren (z.B. aus der Buchungsliste)
    @PostMapping("/generate/{bookingId}")
    public String generateInvoice(@PathVariable Integer bookingId) {
        Invoice invoice = invoiceService.createInvoiceFromBooking(bookingId);
        return "redirect:/invoices/" + invoice.getId(); // Weiterleitung zur Ansicht
    }

    // Ansicht: Rechnung anzeigen
    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Integer id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        model.addAttribute("invoice", invoice);
        return "invoices/view-invoice";
    }
}