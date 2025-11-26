package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.Booking;
import com.eventaro.Eventaro.model.BookingType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PriceCalculationService {

    /**
     * Berechnet den Gesamtpreis der Buchung basierend auf Typ und Rabatten.
     * WI #81: Apply negotiated prices
     * WI #54: Support group/agency booking
     */
    public double calculateTotalPrice(Booking booking) {
        double basePrice = booking.getStandardPricePerPerson();
        int count = booking.getParticipantCount();

        // 1. Priorität: Verhandelter Preis (für Firmen/Agenturen) überschreibt alles
        if (booking.getNegotiatedPricePerPerson() != null) {
            return booking.getNegotiatedPricePerPerson() * count;
        }

        // 2. Logik für Gruppen: Rabattstaffel (Beispiel)
        if (booking.getType() == BookingType.GROUP) {
            double discount = 0.0;
            if (count >= 20) {
                discount = 0.20; // 20% Rabatt ab 20 Personen
            } else if (count >= 10) {
                discount = 0.10; // 10% Rabatt ab 10 Personen
            }
            // Preis * Menge * (1 - Rabatt)
            return (basePrice * count) * (1.0 - discount);
        }

        // 3. Standardfall (Individual)
        return basePrice * count;
    }

    /**
     * Berechnet die Stornokosten basierend auf dem Zeitpunkt der Absage.
     * WI #60: Manage cancellation deadline & calculate resulting costs
     */
    public double calculateCancellationFee(Booking booking, LocalDate cancellationDate) {
        if (!booking.isCancelled()) {
            return 0.0;
        }

        LocalDate eventDate = booking.getEventDateTime().toLocalDate();
        long daysUntilEvent = ChronoUnit.DAYS.between(cancellationDate, eventDate);

        double totalPrice = calculateTotalPrice(booking);

        // Logik laut Anforderung:
        // > 4 Wochen (28 Tage): 0%
        // bis 2 Wochen (14-27 Tage): 25%
        // bis 3 Tage (3-13 Tage): 70%
        // < 3 Tage: 100%

        if (daysUntilEvent >= 28) {
            return 0.0; // Kostenlos
        } else if (daysUntilEvent >= 14) {
            return totalPrice * 0.25; // 25% Gebühr
        } else if (daysUntilEvent >= 3) {
            return totalPrice * 0.70; // 70% Gebühr
        } else {
            return totalPrice; // 100% Gebühr (zu kurzfristig)
        }
    }
}