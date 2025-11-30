package com.eventaro.Eventaro.application.notification;

import com.eventaro.Eventaro.application.notification.dto.NotificationDTO;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendBookingConfirmation(NotificationDTO notification) {
        System.out.println("========== E-MAIL SIMULATION (Ausgang) ==========");
        System.out.println("An:      " + (notification.getEmail() != null ? notification.getEmail() : "keine@email.com"));
        System.out.println("Betreff: Buchungsbestätigung " + notification.getBookingNumber());
        System.out.println("------------------------------------------------");
        System.out.println("Hallo " + notification.getGuestName() + ",");
        System.out.println("Vielen Dank für deine Buchung bei Eventaro!");
        System.out.println("Event: " + notification.getEventName());
        System.out.println("Zeit:  " + notification.getEventDateTime());
        System.out.println("Wir freuen uns auf dich.");
        System.out.println("================================================");
    }

    public void sendCancellationNotification(NotificationDTO notification) {
        System.out.println("========== E-MAIL SIMULATION (Ausgang) ==========");
        System.out.println("An:      " + (notification.getEmail() != null ? notification.getEmail() : "keine@email.com"));
        System.out.println("Betreff: Stornierung deiner Buchung " + notification.getBookingNumber());
        System.out.println("------------------------------------------------");
        System.out.println("Hallo " + notification.getGuestName() + ",");
        System.out.println("deine Buchung für '" + notification.getEventName() + "' wurde storniert.");

        if (notification.getCancellationFee() != null && notification.getCancellationFee() > 0) {
            System.out.println("Bitte beachte, dass eine Stornogebühr von " + String.format("%.2f", notification.getCancellationFee()) + " EUR angefallen ist.");
        } else {
            System.out.println("Die Stornierung erfolgte kostenlos.");
        }
        System.out.println("================================================");
    }
}