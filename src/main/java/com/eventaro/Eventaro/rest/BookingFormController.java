package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.datatransfer.CreateBookingRequest;
import com.eventaro.Eventaro.domain.model.Customer;
import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.enums.Country;
import com.eventaro.Eventaro.enums.PaymentMethod;
import com.eventaro.Eventaro.persistence.CustomerRepository;
import com.eventaro.Eventaro.service.BookingService;
import com.eventaro.Eventaro.service.EventService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingFormController {

    private final BookingService bookingService;
    private final EventService eventService;
    private final CustomerRepository customerRepository; // Repository injizieren für Auto-Fill

    public BookingFormController(BookingService bookingService,
                                 EventService eventService,
                                 CustomerRepository customerRepository) {
        this.bookingService = bookingService;
        this.eventService = eventService;
        this.customerRepository = customerRepository;
    }

    // 1. Formular zum Buchen anzeigen
    @GetMapping("/create/{eventId}")
    public String showBookingForm(@PathVariable Integer eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(eventId);
        request.setTicketCount(1);

        // --- AUTO-FILL START ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            // Versuchen, den Customer anhand der Email (Username) zu finden
            Optional<Customer> customerOpt = customerRepository.findByEmail(username);
            if (customerOpt.isPresent()) {
                Customer c = customerOpt.get();
                request.setFirstName(c.getFirstName());
                request.setLastName(c.getLastName());
                request.setEmail(c.getEmail());
                request.setPhoneNumber(c.getPhoneNumber());

                if (c.getAddress() != null) {
                    request.getAddress().setStreet(c.getAddress().getStreet());
                    request.getAddress().setHouseNumber(c.getAddress().getHousenumber() != null ? c.getAddress().getHousenumber().toString() : "");
                    request.getAddress().setPostalCode(c.getAddress().getZipCode() != null ? c.getAddress().getZipCode().toString() : "");
                    request.getAddress().setCity(c.getAddress().getCity());
                    request.getAddress().setCountry(c.getAddress().getCountry());
                }
            }
        }
        // --- AUTO-FILL END ---

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
            Event event = eventService.getEventById(request.getEventId());
            model.addAttribute("event", event);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("countries", Country.values());
            return "bookings/create-booking";
        }

        try {
            bookingService.createBooking(request);
            // Nach erfolgreicher Buchung zur Homepage oder Bestätigung
            return "redirect:/?bookingSuccess";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            Event event = eventService.getEventById(request.getEventId());
            model.addAttribute("event", event);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("countries", Country.values());
            return "bookings/create-booking";
        }
    }

    // 3. Check-In Action
    @PostMapping("/checkin/{id}")
    public String checkInGuest(@PathVariable Integer id) {
        bookingService.checkInGuest(id);
        return "redirect:/bookings/today";
    }
}