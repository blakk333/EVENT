package com.eventaro.Eventaro.domain.agency;

import com.eventaro.Eventaro.enums.BookingType;
import jakarta.persistence.*;



@Entity
@Table(name = "corporate_clients")
public class CorporateClient {

    @Column(unique = true)
    private String bookingCode; // z.B. "FIRM-MUELLER"

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // z.B. "Reisebüro Müller"
    private String contactPerson;
    private String email;
    private String address;

    @Enumerated(EnumType.STRING)
    private BookingType type; // AGENCY oder COMPANY

    // WI #81: Automatisch verhandelter Preis/Rabatt
    // Wir speichern hier den Rabatt als Dezimalwert (z.B. 0.15 für 15%)
    private Double negotiatedDiscount;

    public CorporateClient() {}

    public CorporateClient(String name, BookingType type, Double negotiatedDiscount) {
        this.name = name;
        this.type = type;
        this.negotiatedDiscount = negotiatedDiscount;
    }

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BookingType getType() { return type; }
    public void setType(BookingType type) { this.type = type; }
    public Double getNegotiatedDiscount() { return negotiatedDiscount; }
    public void setNegotiatedDiscount(Double negotiatedDiscount) { this.negotiatedDiscount = negotiatedDiscount; }
    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
}
