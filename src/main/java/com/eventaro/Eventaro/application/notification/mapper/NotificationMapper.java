package com.eventaro.Eventaro.application.notification.mapper;

import com.eventaro.Eventaro.application.notification.dto.NotificationDTO;
import com.eventaro.Eventaro.domain.booking.Booking;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toDto(Booking booking) {
        return toDto(booking, null);
    }

    public NotificationDTO toDto(Booking booking, Double fee) {
        if (booking == null) return null;

        NotificationDTO dto = new NotificationDTO();
        dto.setEmail(booking.getEmail());
        dto.setBookingNumber(booking.getBookingNumber());
        dto.setGuestName(booking.getGuestName());
        dto.setEventName(booking.getEventName());
        dto.setEventDateTime(booking.getEventDateTime());
        dto.setCancellationFee(fee);

        return dto;
    }
}