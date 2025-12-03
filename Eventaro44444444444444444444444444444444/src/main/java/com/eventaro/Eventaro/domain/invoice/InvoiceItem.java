package com.eventaro.Eventaro.domain.invoice;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    private String id; // Wir nutzen die UUID als Datenbank-ID

    private String description;
    private int quantity;
    private double unitPrice;
    private double vatRate;

    // Leerer Konstruktor für JPA
    public InvoiceItem() {}

    public InvoiceItem(String description, int quantity, double unitPrice, double vatRate) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.vatRate = vatRate;
    }

    public double getTotalNet() { return quantity * unitPrice; }
    public double getVatAmount() { return getTotalNet() * vatRate; }
    public double getTotalGross() { return getTotalNet() + getVatAmount(); }

    // Getter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getVatRate() { return vatRate; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }
}