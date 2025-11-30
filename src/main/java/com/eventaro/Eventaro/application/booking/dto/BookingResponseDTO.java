package com.eventaro.Eventaro.application.booking.dto;

import com.eventaro.Eventaro.enums.BookingType;
import java.time.LocalDateTime;

public class BookingResponseDTO {
    private Long id;
    private String bookingNumber;
    private String guestName;
    private String email;
    private String eventName;
    private LocalDateTime eventDateTime;
    private BookingType type;
    private int participantCount;

    // Status & Finanzen
    private boolean checkedIn;
    private boolean paid;
    private boolean checkedOut;
    private boolean cancelled;
    private Double depositAmount;
    private Double totalPrice; // Berechneter Preis (optional, falls benötigt)

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }
    public BookingType getType() { return type; }
    public void setType(BookingType type) { this.type = type; }
    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public boolean isCheckedOut() { return checkedOut; }
    public void setCheckedOut(boolean checkedOut) { this.checkedOut = checkedOut; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public Double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(Double depositAmount) { this.depositAmount = depositAmount; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}