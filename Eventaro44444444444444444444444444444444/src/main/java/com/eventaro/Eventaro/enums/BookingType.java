package com.eventaro.Eventaro.enums;

// WI #54: Support group, individuals, agency booking
public enum BookingType {
    INDIVIDUAL, // Einzelperson (Standardpreis)
    GROUP,      // Gruppe (Mengenrabatt möglich)
    COMPANY,    // Firma (Verhandelter Preis)
    AGENCY      // Agentur (Verhandelter Preis + Kontingente)
}