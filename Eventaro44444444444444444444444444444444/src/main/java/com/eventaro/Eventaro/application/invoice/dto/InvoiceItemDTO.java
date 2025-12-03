package com.eventaro.Eventaro.application.invoice.dto;

public class InvoiceItemDTO {
    private String id;
    private String description;
    private int quantity;
    private double unitPrice;
    private double totalNet; // Bereits berechnet vom Mapper

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotalNet() { return totalNet; }
    public void setTotalNet(double totalNet) { this.totalNet = totalNet; }
}