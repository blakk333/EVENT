package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.datatransfer.CreateBookingRequest;
import com.eventaro.Eventaro.domain.model.AdditionalService;
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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/frontoffice")
public class FrontOfficeController {

    private final EventService eventService;
    private final BookingService bookingService;

    public FrontOfficeController(EventService eventService, BookingService bookingService) {
        this.eventService = eventService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String frontOfficeDashboard(Model model) {
        model.addAttribute("bookings", bookingService.getBookingsForToday());
        model.addAttribute("todayDate", LocalDate.now());
        return "frontoffice/dashboard";
    }

    @GetMapping("/walkin")
    public String showWalkInSelector(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "frontoffice/walkin-select";
    }

    @GetMapping("/walkin/{eventId}")
    public String showWalkInForm(@PathVariable Integer eventId, Model model) {
        Event event = eventService.getEventById(eventId);

        // Achtung: Hier vereinfacht. Normalerweise müsste man eine Date-Selection machen,
        // da Event jetzt mehrere Termine hat. Wir nehmen hier einfach den ersten Termin
        // oder man müsste den Controller erweitern, um erst das Event und dann das Datum zu wählen.
        // Für den Moment nehmen wir an, wir buchen auf den ersten verfügbaren Termin:
        Integer eventDateId = event.getDates().isEmpty() ? null : event.getDates().get(0).getId();

        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(eventId);
        request.setEventDateId(eventDateId); // Wichtig für den neuen Service!
        request.setTicketCount(1);

        model.addAttribute("bookingRequest", request);
        model.addAttribute("event", event);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("countries", Country.values());

        return "frontoffice/walkin-form";
    }

    @PostMapping("/walkin")
    public String performWalkIn(@ModelAttribute("bookingRequest") @Valid CreateBookingRequest request,
                                BindingResult binding,
                                @RequestParam(value = "manualPrice", required = false) Double manualPrice,
                                Model model) {

        if (binding.hasErrors()) {
            Event event = eventService.getEventById(request.getEventId());
            model.addAttribute("event", event);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("countries", Country.values());
            return "frontoffice/walkin-form";
        }

        try {
            bookingService.createWalkInBooking(request, manualPrice);
            return "redirect:/frontoffice/dashboard?success=walkin";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            Event event = eventService.getEventById(request.getEventId());
            model.addAttribute("event", event);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("countries", Country.values());
            return "frontoffice/walkin-form";
        }
    }

    // --- NEU: Zusatzleistungen anzeigen ---
    @GetMapping("/booking/{id}/addons")
    public String showAddOns(@PathVariable Integer id, Model model) {
        Booking booking = bookingService.getBookingById(id);

        // Verfügbare Services des Events laden
        List<AdditionalService> availableServices = booking.getEvent().getAdditionalServices();

        model.addAttribute("booking", booking);
        model.addAttribute("availableServices", availableServices);

        return "frontoffice/booking-addons";
    }

    // --- NEU: Zusatzleistungen speichern ---
    @PostMapping("/booking/{id}/addons")
    public String saveAddOns(@PathVariable Integer id, @RequestParam(value = "selectedServices", required = false) List<Integer> selectedServices) {
        if (selectedServices != null && !selectedServices.isEmpty()) {
            bookingService.addAdditionalServicesToBooking(id, selectedServices);
        }
        return "redirect:/frontoffice/dashboard?success=addons";
    }
}