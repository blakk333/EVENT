package com.eventaro.Eventaro.enums;

public enum BookingStatus {
    CONFIRMED,      // Fest gebucht
    CANCELLED,      // Storniert
    CHECKED_IN,     // Gast ist anwesend (für Front-Office später)
    PENDING         // Wartet auf Bestätigung/Zahlung
}