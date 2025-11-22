package com.eventaro.Eventaro.domain.model;

import com.eventaro.Eventaro.enums.BookingStatus;
import com.eventaro.Eventaro.enums.PaymentMethod;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String bookingNumber;

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

    // --- NEU: Liste der gebuchten Zusatzleistungen ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "booking_additional_services",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<AdditionalService> bookedServices = new ArrayList<>();
    // ------------------------------------------------

    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    // --- NEUE METHODE ---
    public void addService(AdditionalService service) {
        this.bookedServices.add(service);
        // Preis aktualisieren nicht vergessen!
        this.totalPrice += service.getPrice();
    }

    // Getter und Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }
    public EventDate getEventDate() { return eventDate; }
    public void setEventDate(EventDate eventDate) { this.eventDate = eventDate; }

    // Helper für Legacy-Support
    public Event getEvent() {
        return eventDate != null ? eventDate.getEvent() : null;
    }

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

    public List<AdditionalService> getBookedServices() { return bookedServices; }
    public void setBookedServices(List<AdditionalService> bookedServices) { this.bookedServices = bookedServices; }
}