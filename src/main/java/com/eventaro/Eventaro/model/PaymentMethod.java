package com.eventaro.Eventaro.model;

public enum PaymentMethod {
    CASH("Barzahlung"),
    CARD("Kartenzahlung"),
    TRANSFER("Überweisung"),
    ON_ACCOUNT("Auf Rechnung"); // Für Firmen/Agenturen

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}