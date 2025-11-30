package com.eventaro.Eventaro.domain.booking;

import com.eventaro.Eventaro.enums.BookingType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bookingNumber;

    private String guestName;
    private String email;
    private String eventName;
    private LocalDateTime eventDateTime;

    // --- NEU: Geleistete Anzahlung ---
    private Double depositAmount = 0.0;

    @Enumerated(EnumType.STRING)
    private BookingType type;

    private Integer rating;
    private String feedbackComment;

    private int participantCount;
    private double standardPricePerPerson;
    private Double negotiatedPricePerPerson;

    private boolean checkedIn;
    private boolean paid;
    private boolean checkedOut;
    private boolean invoiceGenerated;
    private boolean cancelled;

    public Booking() {}

    public Booking(String bookingNumber, String guestName, String email, String eventName,
                   LocalDateTime eventDateTime, BookingType type,
                   int participantCount, double standardPricePerPerson) {
        this.bookingNumber = bookingNumber;
        this.guestName = guestName;
        this.email = email;
        this.eventName = eventName;
        this.eventDateTime = eventDateTime;
        this.type = type;
        this.participantCount = participantCount;
        this.standardPricePerPerson = standardPricePerPerson;
        this.checkedIn = false;
        this.paid = false;
        this.cancelled = false;
        this.depositAmount = 0.0;
    }

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // --- NEU: Getter & Setter für depositAmount ---
    public Double getDepositAmount() { return depositAmount != null ? depositAmount : 0.0; }
    public void setDepositAmount(Double depositAmount) { this.depositAmount = depositAmount; }

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

    public double getStandardPricePerPerson() { return standardPricePerPerson; }
    public void setStandardPricePerPerson(double standardPricePerPerson) { this.standardPricePerPerson = standardPricePerPerson; }

    public Double getNegotiatedPricePerPerson() { return negotiatedPricePerPerson; }
    public void setNegotiatedPricePerPerson(Double negotiatedPricePerPerson) { this.negotiatedPricePerPerson = negotiatedPricePerPerson; }

    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public boolean isCheckedOut() { return checkedOut; }
    public void setCheckedOut(boolean checkedOut) { this.checkedOut = checkedOut; }

    public boolean isInvoiceGenerated() { return invoiceGenerated; }
    public void setInvoiceGenerated(boolean invoiceGenerated) { this.invoiceGenerated = invoiceGenerated; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getFeedbackComment() { return feedbackComment; }
    public void setFeedbackComment(String feedbackComment) { this.feedbackComment = feedbackComment; }
}