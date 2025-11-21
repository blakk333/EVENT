package com.eventaro.Eventaro.datatransfer;

import com.eventaro.Eventaro.enums.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressDTO {

    @NotBlank(message = "Street must not be empty.")
    @Size(min = 2, max = 80, message = "Street must be between 2 and 80 characters.")
    private String street;


    @Size(min = 0, max = 10, message = "House number must be between 1 and 10 characters.")
    private String houseNumber;

    @NotBlank(message = "ZIP code must not be empty.")
    @Size(min = 2, max = 15, message = "ZIP code must be between 2 and 15 characters.")
    // Optional: simple ZIP pattern (alphanumeric + dash/space)
    @Pattern(regexp = "^[A-Za-z0-9\\-\\s]+$", message = "ZIP code contains invalid characters.")
    private String postalCode;

    @NotBlank(message = "City must not be empty.")
    @Size(min = 2, max = 80, message = "City must be between 2 and 80 characters.")
    private String city;

    @NotNull(message = "Please select a Country.")
    private Country country;

    // getters / setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }
}
