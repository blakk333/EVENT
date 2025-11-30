package com.eventaro.Eventaro.application.booking.dto;

import com.eventaro.Eventaro.enums.BookingType;
import java.time.LocalDateTime;

public class BookingRequest {
    // Gast-Infos
    private String firstName;
    private String lastName;
    private String email;

    // Event-Infos (wird oft direkt übergeben)
    private Long eventId;
    private String eventName;
    private LocalDateTime eventDateTime;
    private Double standardPrice;

    // Buchungs-Details
    private int participantCount;
    private BookingType type;
    private String partnerCode; // Optional für Firmen

    // Getter & Setter
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }
    public Double getStandardPrice() { return standardPrice; }
    public void setStandardPrice(Double standardPrice) { this.standardPrice = standardPrice; }
    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
    public BookingType getType() { return type; }
    public void setType(BookingType type) { this.type = type; }
    public String getPartnerCode() { return partnerCode; }
    public void setPartnerCode(String partnerCode) { this.partnerCode = partnerCode; }

    // Helper für den vollen Namen
    public String getGuestName() { return firstName + " " + lastName; }
}