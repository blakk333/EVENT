package com.eventaro.Eventaro.domain.event;

import jakarta.persistence.Embeddable;

@Embeddable
public class AddOn {
    private String name;
    private double price;

    // Leerer Konstruktor für JPA (Pflicht)
    public AddOn() {}

    public AddOn(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // Getter & Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}