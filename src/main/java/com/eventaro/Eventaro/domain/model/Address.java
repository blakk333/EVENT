package com.eventaro.Eventaro.domain.model;

import com.eventaro.Eventaro.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Embeddable
public class Address {

    @NotBlank
    @Size(min = 2, max = 80)
    @Column(name = "loc_street")
    private String street;

    @NotNull
    @Column(name = "loc_house_number")
    private Integer houseNumber;

    @NotNull
    @Min(1000) @Max(99999)
    @Column(name = "loc_postal_code")
    private Integer zipCode;

    @NotBlank
    @Column(name = "loc_city")
    private String city;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loc_country", nullable = false)
    private Country country;


    public Address() {}

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getHousenumber() {
        return houseNumber;
    }

    public void setHousenumber(Integer houseNumber) {
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
}