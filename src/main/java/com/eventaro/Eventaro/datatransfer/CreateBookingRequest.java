package com.eventaro.Eventaro.datatransfer;

import com.eventaro.Eventaro.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class CreateBookingRequest {

    @NotNull
    private Integer eventId;

    @Min(1) @Max(50)
    private Integer ticketCount;

    @NotNull
    private PaymentMethod paymentMethod;

    // Customer Details
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email @NotBlank
    private String email;
    private String phoneNumber;

    @Valid
    private AddressDTO address = new AddressDTO(); // Dein existierendes AddressDTO nutzen

    // Getter und Setter
    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }
    public Integer getTicketCount() { return ticketCount; }
    public void setTicketCount(Integer ticketCount) { this.ticketCount = ticketCount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public AddressDTO getAddress() { return address; }
    public void setAddress(AddressDTO address) { this.address = address; }
}