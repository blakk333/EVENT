package com.eventaro.Eventaro.domain.address;

import com.eventaro.Eventaro.enums.Country;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Address {

    private String street;
    private Integer houseNumber; // Im DTO ist es String, hier Integer (laut Mapper-Logik)
    private Integer zipCode;     // Im DTO "postalCode", hier "zipCode"
    private String city;

    @Enumerated(EnumType.STRING)
    private Country country;

    // Leerer Konstruktor (wichtig für Frameworks)
    public Address() {
    }

    // Konstruktor mit Feldern
    public Address(String street, Integer houseNumber, Integer zipCode, String city, Country country) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    // Getter und Setter
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return street + " " + (houseNumber != null ? houseNumber : "") + ", " +
                (zipCode != null ? zipCode : "") + " " + city + ", " +
                (country != null ? country.getDisplayName() : "");
    }
}