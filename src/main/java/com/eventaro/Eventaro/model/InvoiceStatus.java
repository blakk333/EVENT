package com.eventaro.Eventaro.model;

public enum InvoiceStatus {
    DRAFT,  // Zwischenrechnung (änderbar) -> WI #68
    FINAL,  // Endrechnung (festgeschrieben) -> WI #67
    PAID    // Bezahlt
}