package com.eventaro.payment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class PaymentController {

    // Seite anzeigen: Wird von deiner Haupt-App aufgerufen
    @GetMapping("/pay")
    public String showPaymentPage(@RequestParam String ref,
                                  @RequestParam String amount,
                                  @RequestParam String callback,
                                  Model model) {
        // Daten an das Template übergeben
        model.addAttribute("reference", ref);
        model.addAttribute("amount", amount);
        model.addAttribute("callbackUrl", callback);
        return "payment";
    }

    // Zahlung verarbeiten (Simuliert Klick auf "Bezahlen")
    @PostMapping("/process")
    public String processPayment(@RequestParam String callbackUrl,
                                 @RequestParam String reference) {

        // Künstliche Verzögerung für Realismus (1-2 Sekunden)
        try {
            Thread.sleep(1000 + new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 90% Erfolgsquote berechnen
        // Random liefert Wert zwischen 0.0 und 1.0
        boolean success = Math.random() < 0.90;

        String status = success ? "SUCCESS" : "FAILURE";

        // Sicherer Redirect zurück zur Haupt-App mit dem Ergebnis
        // Wir hängen status und Referenz an, damit die Haupt-App weiß, was passiert ist
        return "redirect:" + callbackUrl + "?status=" + status + "&ref=" + reference;
    }
}