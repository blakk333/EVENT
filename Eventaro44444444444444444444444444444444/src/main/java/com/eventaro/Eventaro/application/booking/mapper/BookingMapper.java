package com.eventaro.Eventaro.application.booking.mapper;

import com.eventaro.Eventaro.application.booking.dto.BookingRequest;
import com.eventaro.Eventaro.application.booking.dto.BookingResponseDTO;
import com.eventaro.Eventaro.domain.booking.Booking;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class BookingMapper {

    public BookingResponseDTO toResponse(Booking entity) {
        if (entity == null) return null;

        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(entity.getId());
        dto.setBookingNumber(entity.getBookingNumber());
        dto.setGuestName(entity.getGuestName());
        dto.setEmail(entity.getEmail());
        dto.setEventName(entity.getEventName());
        dto.setEventDateTime(entity.getEventDateTime());
        dto.setType(entity.getType());
        dto.setParticipantCount(entity.getParticipantCount());

        dto.setCheckedIn(entity.isCheckedIn());
        dto.setPaid(entity.isPaid());
        dto.setCheckedOut(entity.isCheckedOut());
        dto.setCancelled(entity.isCancelled());
        dto.setDepositAmount(entity.getDepositAmount());

        // Preisberechnung für Anzeige
        double pricePerPerson = entity.getNegotiatedPricePerPerson() != null
                ? entity.getNegotiatedPricePerPerson()
                : entity.getStandardPricePerPerson();
        dto.setTotalPrice(pricePerPerson * entity.getParticipantCount());

        return dto;
    }

    public Booking toEntity(BookingRequest request) {
        if (request == null) return null;

        // Logik für Booking-Nummer Erzeugung könnte auch in den Service,
        // aber hier ist es ein guter Platz für die Initialisierung.
        String bookingNum = "B-" + (10000 + new Random().nextInt(90000));

        return new Booking(
                bookingNum,
                request.getGuestName(),
                request.getEmail(),
                request.getEventName(),
                request.getEventDateTime(),
                request.getType(),
                request.getParticipantCount(),
                request.getStandardPrice() != null ? request.getStandardPrice() : 0.0
        );
    }
}