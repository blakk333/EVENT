package com.eventaro.Eventaro.domain.invoice;


import com.eventaro.Eventaro.enums.PaymentMethod;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.eventaro.Eventaro.enums.InvoiceStatus;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    private String id;

    private String bookingRef;
    private String invoiceNumber;
    private String recipientName;
    private String recipientAddress;
    private LocalDate date;

    // --- NEU: Übernommene Anzahlung aus Buchung ---
    private Double advancePayment = 0.0;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    // --- NEU: Zahlungsart ---
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceItem> items = new ArrayList<>();

    public Invoice() {}

    public Invoice(String bookingRef, String recipientName, String recipientAddress) {
        this.id = UUID.randomUUID().toString();
        this.bookingRef = bookingRef;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.date = LocalDate.now();
        this.status = InvoiceStatus.DRAFT;
    }

    public double getTotalNet() { return items.stream().mapToDouble(InvoiceItem::getTotalNet).sum(); }
    public double getTotalVat() { return items.stream().mapToDouble(InvoiceItem::getVatAmount).sum(); }
    public double getTotalGross() { return items.stream().mapToDouble(InvoiceItem::getTotalGross).sum(); }

    // --- NEU: Restbetrag berechnen ---
    public double getRemainingAmount() {
        return getTotalGross() - (advancePayment != null ? advancePayment : 0.0);
    }

    public void addItem(InvoiceItem item) { this.items.add(item); }
    public void removeItem(InvoiceItem item) { this.items.remove(item); }

    // Getter / Setter
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
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }

    // --- NEU: Getter & Setter für Neue Felder ---
    public Double getAdvancePayment() { return advancePayment != null ? advancePayment : 0.0; }
    public void setAdvancePayment(Double advancePayment) { this.advancePayment = advancePayment; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}