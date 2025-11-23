package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.datatransfer.CreateBookingRequest;
import com.eventaro.Eventaro.domain.model.Booking;
import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.enums.Country;
import com.eventaro.Eventaro.enums.PaymentMethod;
import com.eventaro.Eventaro.service.BookingService;
import com.eventaro.Eventaro.service.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookings")
public class BookingFormController {

    private final BookingService bookingService;
    private final EventService eventService;

    public BookingFormController(BookingService bookingService, EventService eventService) {
        this.bookingService = bookingService;
        this.eventService = eventService;
    }

    // 1. Formular zum Buchen anzeigen
    @GetMapping("/create/{eventId}")
    public String showBookingForm(@PathVariable Integer eventId, Model model) {
        Event event = eventService.getEventById(eventId);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(eventId);
        request.setTicketCount(1); // Standardwert

        model.addAttribute("bookingRequest", request);
        model.addAttribute("event", event);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("countries", Country.values());

        return "bookings/create-booking";
    }

    // 2. Buchung speichern (POST)
    @PostMapping("/create")
    public String createBooking(@ModelAttribute("bookingRequest") @Valid CreateBookingRequest request,
                                BindingResult binding,
                                Model model) {

        if (binding.hasErrors()) {
            // Bei Fehlern Formular neu laden mit den alten Eingaben
            Event event = eventService.getEventById(request.getEventId());
            model.addAttribute("event", event);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("countries", Country.values());
            return "bookings/create-booking";
        }

        try {
            // Änderung: Service gibt das Booking zurück, damit wir die ID haben
            Booking booking = bookingService.createBooking(request);

            // Änderung: Redirect zur öffentlichen Bestätigungsseite
            return "redirect:/bookings/confirmation/" + booking.getId();

        } catch (Exception e) {
            // Fehler (z.B. ausgebucht) im Formular anzeigen
            model.addAttribute("errorMessage", e.getMessage());
            Event event = eventService.getEventById(request.getEventId());
            model.addAttribute("event", event);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("countries", Country.values());
            return "bookings/create-booking";
        }
    }

    // 3. NEU: Öffentliche Bestätigungsseite
    @GetMapping("/confirmation/{bookingId}")
    public String showConfirmation(@PathVariable Integer bookingId, Model model) {
        // Hier könnte man das Booking laden, um Details anzuzeigen
        // Wir übergeben die ID, falls man sie im Template braucht
        model.addAttribute("bookingId", bookingId);
        return "public/booking-success";
    }

    // 4. Check-In Action (Backoffice Funktionalität)
    @PostMapping("/checkin/{id}")
    public String checkInGuest(@PathVariable Integer id) {
        bookingService.checkInGuest(id);
        return "redirect:/bookings/today";
    }
}