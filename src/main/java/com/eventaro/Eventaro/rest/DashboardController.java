package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.domain.model.Booking;
import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.enums.EventStatus;
import com.eventaro.Eventaro.service.BookingService;
import com.eventaro.Eventaro.service.EventService; // <--- DIESER IMPORT FEHLTE
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private final EventService eventService;
    private final BookingService bookingService;

    public DashboardController(EventService eventService, BookingService bookingService) {
        this.eventService = eventService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 1. User Info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        model.addAttribute("todayDate", LocalDate.now());

        // 2. Statistiken laden
        List<Event> allEvents = eventService.getAllEvents();
        List<Booking> todaysBookings = bookingService.getBookingsForToday();

        // KPI: Anzahl Events Gesamt
        model.addAttribute("totalEvents", allEvents.size());

        // KPI: Anzahl Buchungen heute
        model.addAttribute("todaysBookingsCount", todaysBookings.size());

        // KPI: Aktive Events (Published)
        long activeEvents = allEvents.stream()
                .filter(e -> e.getStatusOfEvent() == EventStatus.PUBLISHED)
                .count();
        model.addAttribute("activeEvents", activeEvents);

        // KPI: Umsatz-Vorschau heute
        double revenueToday = todaysBookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        model.addAttribute("revenueToday", revenueToday);

        // 3. Dashboard-View aufrufen
        return "dashboard/dashboard";
    }
}