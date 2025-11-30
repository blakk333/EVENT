package com.eventaro.Eventaro.enums;

public enum Country {
    AUSTRIA("Österreich"),
    GERMANY("Deutschland"),
    SWITZERLAND("Schweiz"),
    ITALY("Italien"),
    FRANCE("Frankreich"),
    OTHER("Anderes Land");

    private final String displayName;

    Country(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}