package com.eventaro.Eventaro.domain.model;

import com.eventaro.Eventaro.enums.BookingStatus;
import com.eventaro.Eventaro.enums.PaymentMethod;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String bookingNumber;

    // --- GEÄNDERT: Statt Event referenzieren wir jetzt den konkreten Termin ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_date_id", nullable = false)
    private EventDate eventDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Integer ticketCount;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private LocalDateTime bookingDate;

    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    // Getter und Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    // --- NEUE GETTER/SETTER ---
    public EventDate getEventDate() { return eventDate; }
    public void setEventDate(EventDate eventDate) { this.eventDate = eventDate; }

    // Hilfsmethode: Damit alter Code wie booking.getEvent().getName() leichter repariert werden kann
    public Event getEvent() {
        if (eventDate != null) {
            return eventDate.getEvent();
        }
        return null;
    }

    // Restliche Getter/Setter
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Integer getTicketCount() { return ticketCount; }
    public void setTicketCount(Integer ticketCount) { this.ticketCount = ticketCount; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
}