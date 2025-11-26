package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.model.Booking;
import com.eventaro.Eventaro.model.BookingType;
import com.eventaro.Eventaro.model.CorporateClient;
import com.eventaro.Eventaro.model.StatusOfEvent;
import com.eventaro.Eventaro.repository.EventRepository;
import com.eventaro.Eventaro.service.AgencyService; // <--- NEU
import com.eventaro.Eventaro.service.BookingService;
import com.eventaro.Eventaro.service.CategoryService;
import com.eventaro.Eventaro.service.EmailService;
import com.eventaro.Eventaro.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Controller
public class PublicWebsiteController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AgencyService agencyService; // <--- NEU: Service für Partner-Check

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("events", eventService.getPublishedEvents());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "public/home";
    }

    @GetMapping("/public/events")
    public String allEvents(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.isEmpty()) {
            // Filterlogik: Nur Events der gewählten Kategorie anzeigen
            model.addAttribute("events", eventRepository.findByStatusAndCategory(StatusOfEvent.PUBLISHED, category));
            model.addAttribute("activeCategory", category);
        } else {
            // Alle anzeigen (WI #79: Hier könnte man später noch .sorted() hinzufügen)
            model.addAttribute("events", eventService.getPublishedEvents());
        }
        return "public/events";
    }

    @GetMapping("/public/book/{id}")
    public String showBookingForm(@PathVariable Long id, Model model) {
        return eventService.getEventById(id).map(event -> {
            model.addAttribute("event", event);
            return "public/booking-form";
        }).orElse("redirect:/public/events");
    }

    @PostMapping("/public/book")
    public String processPublicBooking(@RequestParam Long eventId, // <--- NEU: Wichtig für saubere Referenz
                                       @RequestParam String eventName,
                                       @RequestParam Double standardPrice,
                                       @RequestParam String firstName,
                                       @RequestParam String lastName,
                                       @RequestParam String email,
                                       @RequestParam int participantCount,
                                       @RequestParam String bookingType,
                                       @RequestParam(required = false) String partnerCode, // <--- NEU: Partner Code
                                       @RequestParam String dateString) {

        String bookingNum = "B-" + (10000 + new Random().nextInt(90000));
        String guestName = firstName + " " + lastName;
        BookingType type = BookingType.valueOf(bookingType);

        LocalDateTime eventDate = LocalDateTime.parse(dateString);

        Booking newBooking = new Booking(
                bookingNum, guestName, email, eventName,
                eventDate, type, participantCount, standardPrice
        );

        // --- WI #81 & #54: FIRMENRABATT ANWENDEN ---
        // Wenn es eine Firmenbuchung ist und ein Code eingegeben wurde
        if ((type == BookingType.COMPANY || type == BookingType.AGENCY) && partnerCode != null && !partnerCode.isEmpty()) {

            // 1. Partner anhand Code suchen
            Optional<CorporateClient> clientOpt = agencyService.getClientByBookingCode(partnerCode);

            if (clientOpt.isPresent()) {
                CorporateClient client = clientOpt.get();

                // 2. Event laden um Basispreis sicher zu haben (optional, wir haben standardPrice als Fallback)
                eventService.getEventById(eventId).ifPresent(event -> {

                    // 3. Rabattierten Preis berechnen
                    double discountedPrice = agencyService.calculateDiscountedPrice(event, client.getId());

                    // 4. In Buchung setzen
                    newBooking.setNegotiatedPricePerPerson(discountedPrice);

                    System.out.println(">> B2B RABATT: " + client.getName() + " erhält Preis " + discountedPrice);
                });

            } else {
                // Code ungültig -> Fallback auf Standardpreis (Logik könnte man hier erweitern, z.B. Fehler anzeigen)
                System.out.println(">> WARNUNG: Ungültiger Partner-Code: " + partnerCode + ". Standardpreis wird verwendet.");
                // Optional: Typ auf INDIVIDUAL zurücksetzen, da keine gültige Firma verknüpft ist
                newBooking.setType(BookingType.INDIVIDUAL);
            }
        }

        bookingService.addBooking(newBooking);

        // E-Mail versenden
        emailService.sendBookingConfirmation(newBooking);

        return "redirect:/public/booking-success";
    }

    @GetMapping("/public/booking-success")
    public String bookingSuccess() {
        return "public/booking-success";
    }

    @GetMapping("/public/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "public/categories";
    }

    @GetMapping("/public/about")
    public String about() { return "public/about"; }
}