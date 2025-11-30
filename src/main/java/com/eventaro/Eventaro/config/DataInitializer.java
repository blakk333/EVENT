package com.eventaro.Eventaro.config;

import com.eventaro.Eventaro.domain.event.AddOn;
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.domain.event.Event;
import com.eventaro.Eventaro.enums.BookingType;
import com.eventaro.Eventaro.enums.StatusOfEvent;
import com.eventaro.Eventaro.domain.user.User;
import com.eventaro.Eventaro.domain.booking.BookingRepository;
import com.eventaro.Eventaro.domain.event.EventRepository;
import com.eventaro.Eventaro.domain.user.UserRepository; // NEU
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // NEU

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(EventRepository eventRepo,
                                   BookingRepository bookingRepo,
                                   UserRepository userRepo, // NEU: Repository für User
                                   PasswordEncoder encoder) { // NEU: Encoder für Passwörter
        return args -> {

            // --- SCHRITT 1: BENUTZER INITIALISIEREN ---
            if (userRepo.count() == 0) {
                System.out.println("--- Erstelle Standard-Benutzer ---");

                // Admin (Darf alles)
                User admin = new User("admin", encoder.encode("admin123"), "ADMIN", "System Administrator");
                userRepo.save(admin);

                // Backoffice (Darf Events & Buchungen verwalten)
                User bo = new User("backoffice", encoder.encode("back123"), "BACKOFFICE", "Backoffice Mitarbeiter");
                userRepo.save(bo);

                // Frontoffice (Darf Check-Ins machen & Tagesgeschäft sehen)
                User fo = new User("frontoffice", encoder.encode("front123"), "FRONTOFFICE", "Frontoffice Mitarbeiter");
                userRepo.save(fo);

                System.out.println("--- Benutzer erstellt: admin/admin123, backoffice/back123, frontoffice/front123 ---");
            }

            // --- SCHRITT 2: EVENTS & BUCHUNGEN INITIALISIEREN ---
            // Nur initialisieren, wenn Event-DB leer ist
            if (eventRepo.count() > 0) return;

            System.out.println("--- Initialisiere Datenbank mit Dummy-Daten (Events & Buchungen) ---");

            // 1. Events erstellen
            Event e1 = new Event(null, "Canyoning Einsteiger", "Wassersport Tirol", "Canyoning", "Anfänger", 89.00, 15, StatusOfEvent.PUBLISHED);
            e1.setDescription("Ein spritziges Abenteuer für die ganze Familie.");
            e1.addDate(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
            e1.addAddOn(new AddOn("GoPro Verleih", 25.00));
            eventRepo.save(e1);

            Event e2 = new Event(null, "Klettersteig 'Adler'", "Alpinschule Innsbruck", "Klettern", "Fortgeschritten", 45.50, 8, StatusOfEvent.DRAFT);
            e2.setDescription("Nur für Schwindelfreie!");
            e2.addDate(LocalDateTime.now().plusDays(5).withHour(9).withMinute(0));
            eventRepo.save(e2);

            Event e3 = new Event(null, "Yoga im Park", "Relax Austria", "Yoga", "Anfänger", 25.00, 20, StatusOfEvent.PUBLISHED);
            e3.addDate(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0));
            eventRepo.save(e3);

            // 2. Buchungen erstellen
// Beispiel Anpassung:
            Booking b1 = new Booking("B-1001", "Anna Schmidt", "anna@test.com", "Yoga im Park",
                    LocalDateTime.now().plusDays(1).withHour(18).withMinute(0),
                    BookingType.INDIVIDUAL, 1, 25.00);

            Booking b2 = new Booking("B-1002", "Markus Müller", "markus@test.com", "Canyoning Einsteiger",
                    LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                    BookingType.INDIVIDUAL, 2, 89.00);

            System.out.println("--- Datenbank Initialisierung abgeschlossen ---");
        };
    }
}