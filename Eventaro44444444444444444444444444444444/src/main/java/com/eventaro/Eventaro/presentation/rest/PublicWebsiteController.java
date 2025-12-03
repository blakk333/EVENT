package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.agency.AgencyService;
import com.eventaro.Eventaro.application.agency.dto.CorporateClientResponse; // <--- NEU: DTO Import
import com.eventaro.Eventaro.application.booking.BookingService;
import com.eventaro.Eventaro.application.category.CategoryService;
import com.eventaro.Eventaro.application.event.EventService;
import com.eventaro.Eventaro.application.notification.EmailService;
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.enums.BookingType;
import com.eventaro.Eventaro.domain.event.EventRepository;
import com.eventaro.Eventaro.enums.StatusOfEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.eventaro.Eventaro.application.notification.dto.NotificationDTO; // <--- NEU
import com.eventaro.Eventaro.application.notification.mapper.NotificationMapper; // <--- NEU

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
    private NotificationMapper notificationMapper; // <--- NEU: Mapper injizieren

    @Autowired
    private AgencyService agencyService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("events", eventService.getPublishedEvents());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "public/home";
    }

    @GetMapping("/public/events")
    public String allEvents(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.isEmpty()) {
            model.addAttribute("events", eventRepository.findByStatusAndCategory(StatusOfEvent.PUBLISHED, category));
            model.addAttribute("activeCategory", category);
        } else {
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
    public String processPublicBooking(@RequestParam Long eventId,
                                       @RequestParam String eventName,
                                       @RequestParam Double standardPrice,
                                       @RequestParam String firstName,
                                       @RequestParam String lastName,
                                       @RequestParam String email,
                                       @RequestParam int participantCount,
                                       @RequestParam String bookingType,
                                       @RequestParam(required = false) String partnerCode,
                                       @RequestParam String dateString) {

        String bookingNum = "B-" + (10000 + new Random().nextInt(90000));
        String guestName = firstName + " " + lastName;
        BookingType type = BookingType.valueOf(bookingType);

        LocalDateTime eventDate = LocalDateTime.parse(dateString);

        Booking newBooking = new Booking(
                bookingNum, guestName, email, eventName,
                eventDate, type, participantCount, standardPrice
        );

        // --- FIRMENRABATT ANWENDEN ---
        if ((type == BookingType.COMPANY || type == BookingType.AGENCY) && partnerCode != null && !partnerCode.isEmpty()) {
            Optional<CorporateClientResponse> clientOpt = agencyService.getClientByBookingCode(partnerCode);

            if (clientOpt.isPresent()) {
                CorporateClientResponse client = clientOpt.get();
                eventService.findEventEntityById(eventId).ifPresent(event -> {
                    double discountedPrice = agencyService.calculateDiscountedPrice(event, client.getId());
                    newBooking.setNegotiatedPricePerPerson(discountedPrice);
                    System.out.println(">> B2B RABATT: " + client.getName() + " erhält Preis " + discountedPrice);
                });
            } else {
                System.out.println(">> WARNUNG: Ungültiger Partner-Code: " + partnerCode + ". Standardpreis wird verwendet.");
                newBooking.setType(BookingType.INDIVIDUAL);
            }
        }

        bookingService.addBooking(newBooking);

        // --- FEHLERBEHEBUNG HIER ---
        // Wandle Booking Entity in NotificationDTO um
        NotificationDTO notification = notificationMapper.toDto(newBooking);
        emailService.sendBookingConfirmation(notification);

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