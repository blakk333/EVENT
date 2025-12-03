package com.eventaro.Eventaro.application.notification.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private String email;
    private String bookingNumber;
    private String guestName;
    private String eventName;
    private LocalDateTime eventDateTime;

    // Nur für Stornierungen relevant
    private Double cancellationFee;

    // Getter & Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }
    public Double getCancellationFee() { return cancellationFee; }
    public void setCancellationFee(Double cancellationFee) { this.cancellationFee = cancellationFee; }
}