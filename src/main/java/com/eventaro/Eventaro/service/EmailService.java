package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.Booking;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // WI #63: Send booking confirmation
    public void sendBookingConfirmation(Booking booking) {
        System.out.println("========== E-MAIL SIMULATION (Ausgang) ==========");
        System.out.println("An:      " + (booking.getEmail() != null ? booking.getEmail() : "keine@email.com"));
        System.out.println("Betreff: Buchungsbestätigung " + booking.getBookingNumber());
        System.out.println("------------------------------------------------");
        System.out.println("Hallo " + booking.getGuestName() + ",");
        System.out.println("Vielen Dank für deine Buchung bei Eventaro!");
        System.out.println("Event: " + booking.getEventName());
        System.out.println("Zeit:  " + booking.getEventDateTime());
        System.out.println("Wir freuen uns auf dich.");
        System.out.println("================================================");
    }

    // WI #64: Send information about changes (hier: Stornierung)
    public void sendCancellationNotification(Booking booking, double fee) {
        System.out.println("========== E-MAIL SIMULATION (Ausgang) ==========");
        System.out.println("An:      " + (booking.getEmail() != null ? booking.getEmail() : "keine@email.com"));
        System.out.println("Betreff: Stornierung deiner Buchung " + booking.getBookingNumber());
        System.out.println("------------------------------------------------");
        System.out.println("Hallo " + booking.getGuestName() + ",");
        System.out.println("deine Buchung für '" + booking.getEventName() + "' wurde storniert.");
        if (fee > 0) {
            System.out.println("Bitte beachte, dass eine Stornogebühr von " + String.format("%.2f", fee) + " EUR angefallen ist.");
        } else {
            System.out.println("Die Stornierung erfolgte kostenlos.");
        }
        System.out.println("================================================");
    }
}