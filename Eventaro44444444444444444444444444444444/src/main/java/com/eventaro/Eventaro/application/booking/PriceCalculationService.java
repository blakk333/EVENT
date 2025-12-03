package com.eventaro.Eventaro.application.booking;

import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.enums.BookingType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PriceCalculationService {

    public double calculateTotalPrice(Booking booking) {
        double basePrice = booking.getStandardPricePerPerson();
        int count = booking.getParticipantCount();

        if (booking.getNegotiatedPricePerPerson() != null) {
            return booking.getNegotiatedPricePerPerson() * count;
        }

        if (booking.getType() == BookingType.GROUP) {
            double discount = 0.0;
            if (count >= 20) discount = 0.20;
            else if (count >= 10) discount = 0.10;

            return (basePrice * count) * (1.0 - discount);
        }

        return basePrice * count;
    }

    public double calculateCancellationFee(Booking booking, LocalDate cancellationDate) {
        if (!booking.isCancelled()) return 0.0;

        LocalDate eventDate = booking.getEventDateTime().toLocalDate();
        long daysUntilEvent = ChronoUnit.DAYS.between(cancellationDate, eventDate);
        double totalPrice = calculateTotalPrice(booking);

        if (daysUntilEvent >= 28) return 0.0;
        else if (daysUntilEvent >= 14) return totalPrice * 0.25;
        else if (daysUntilEvent >= 3) return totalPrice * 0.70;
        else return totalPrice;
    }
}