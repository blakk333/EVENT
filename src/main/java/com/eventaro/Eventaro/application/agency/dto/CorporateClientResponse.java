package com.eventaro.Eventaro.application.agency.dto;

import com.eventaro.Eventaro.enums.BookingType;

public class CorporateClientResponse {
    private Long id;
    private String name;
    private String contactPerson;
    private String email;
    private String address;
    private BookingType type;
    private Double negotiatedDiscount;
    private String bookingCode; // Generiertes Feld

    // Getter & Setter
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