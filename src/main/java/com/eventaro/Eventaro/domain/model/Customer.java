package com.eventaro.Eventaro.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email; // Eindeutige Identifizierung

    private String phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="street", column=@Column(name="cust_street")),
            @AttributeOverride(name="houseNumber", column=@Column(name="cust_house_number")),
            @AttributeOverride(name="zipCode", column=@Column(name="cust_zip_code")),
            @AttributeOverride(name="city", column=@Column(name="cust_city")),
            @AttributeOverride(name="country", column=@Column(name="cust_country"))
    })
    private Address address;

    // Leerer Konstruktor, Getter und Setter
    public Customer() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}