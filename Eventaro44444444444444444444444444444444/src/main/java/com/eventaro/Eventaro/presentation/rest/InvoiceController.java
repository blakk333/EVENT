package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.booking.BookingService;
import com.eventaro.Eventaro.application.invoice.InvoiceService;
import com.eventaro.Eventaro.application.invoice.dto.InvoiceResponseDTO;
import com.eventaro.Eventaro.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // WICHTIG: Neuer Import!

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

    // --- NEU: Online Payment Funktionen (WI #Zahlungsschnittstelle) ---

    // 1. Start: Leitet den User zur externen Payment-App (Port 8081)
    @PostMapping("/pay-online")
    public String initiateOnlinePayment(@RequestParam String invoiceId) {
        // Wir laden die Rechnungsdaten, um den Betrag zu wissen
        InvoiceResponseDTO invoice = invoiceService.getInvoiceById(invoiceId);

        // Das ist die URL deiner neuen externen App
        String externalAppUrl = "http://localhost:8081/pay";

        // Das ist die URL, an die die externe App das Ergebnis senden soll
        // (Das ist wieder HIER in dieser App)
        String callbackUrl = "http://localhost:8080/invoices/payment-callback";

        // Wir nutzen die ID als Referenz, da eine Draft-Rechnung noch keine offizielle Rechnungsnummer hat
        String reference = invoice.getId();

        // Wir bauen den Redirect-String
        return "redirect:" + externalAppUrl +
                "?ref=" + reference +
                "&amount=" + invoice.getRemainingAmount() +
                "&callback=" + callbackUrl;
    }

    // 2. Callback: Verarbeitet das Ergebnis, wenn der User zurückkommt
    @GetMapping("/payment-callback")
    public String handlePaymentCallback(@RequestParam String status,
                                        @RequestParam String ref,
                                        RedirectAttributes redirectAttributes) {

        // 'ref' ist unsere Invoice-ID, die wir vorher hingeschickt haben
        String invoiceId = ref;

        if ("SUCCESS".equals(status)) {
            // Wenn erfolgreich: Rechnung abschließen (Methode existiert bereits im Service)
            // Wir nehmen hier einfach mal 'CARD' als Methode an (oder man erweitert PaymentMethod um ONLINE)
            invoiceService.finalizeInvoice(invoiceId, PaymentMethod.CARD);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Online-Zahlung war erfolgreich!");
        } else {
            // Wenn fehlgeschlagen (die 10% Chance)
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Zahlung abgelehnt oder fehlgeschlagen.");
        }

        // Zurück zur Rechnungsansicht
        return "redirect:/invoices/view/" + invoiceId;
    }
}