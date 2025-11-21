package com.eventaro.Eventaro.domain.model;

import com.eventaro.Eventaro.enums.PaymentMethod;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    public enum InvoiceStatus {
        DRAFT, // Vorläufig (Interim)
        FINAL, // Festgeschrieben
        PAID,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // Z.B. INV-2025-001

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    // Wir verknüpfen die Rechnung mit einer Buchung
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Double totalAmount;
    private Double taxAmount; // MwSt.

    // Empfängerdaten kopieren wir fest in die Rechnung (Snapshot),
    // falls der Kunde später umzieht, bleibt die Rechnung historisch korrekt.
    private String recipientName;
    private String recipientAddress;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    public Invoice() {
        this.invoiceDate = LocalDate.now();
        this.status = InvoiceStatus.DRAFT;
    }

    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.totalAmount = items.stream().mapToDouble(InvoiceItem::getTotalAmount).sum();
        // Simpel: 20% MwSt für Österreich als Beispiel
        this.taxAmount = this.totalAmount * 0.20;
    }

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientAddress() { return recipientAddress; }
    public void setRecipientAddress(String recipientAddress) { this.recipientAddress = recipientAddress; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
}