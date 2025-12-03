package com.eventaro.Eventaro.application.invoice.dto;

import com.eventaro.Eventaro.enums.InvoiceStatus;
import com.eventaro.Eventaro.enums.PaymentMethod;
import java.time.LocalDate;
import java.util.List;

public class InvoiceResponseDTO {
    private String id;
    private String bookingRef;
    private String invoiceNumber;
    private String recipientName;
    private String recipientAddress;
    private LocalDate date;
    private InvoiceStatus status;
    private PaymentMethod paymentMethod;
    private Double advancePayment;

    // Berechnete Summen
    private double totalNet;
    private double totalVat;
    private double totalGross;
    private double remainingAmount;

    // Die Liste der Positionen
    private List<InvoiceItemDTO> items;

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientAddress() { return recipientAddress; }
    public void setRecipientAddress(String recipientAddress) { this.recipientAddress = recipientAddress; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public Double getAdvancePayment() { return advancePayment; }
    public void setAdvancePayment(Double advancePayment) { this.advancePayment = advancePayment; }

    public double getTotalNet() { return totalNet; }
    public void setTotalNet(double totalNet) { this.totalNet = totalNet; }
    public double getTotalVat() { return totalVat; }
    public void setTotalVat(double totalVat) { this.totalVat = totalVat; }
    public double getTotalGross() { return totalGross; }
    public void setTotalGross(double totalGross) { this.totalGross = totalGross; }
    public double getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }

    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
}