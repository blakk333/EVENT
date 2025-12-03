package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.booking.BookingService;
import com.eventaro.Eventaro.application.event.EventService;
import com.eventaro.Eventaro.application.invoice.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventService eventService;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("username", "Admin"); // Später aus Security Context
        model.addAttribute("todayDate", LocalDate.now());

        // Echte Daten aus der DB
        model.addAttribute("todaysBookingsCount", bookingService.countTodaysBookings());
        model.addAttribute("revenueToday", invoiceService.calculateRevenueToday());
        model.addAttribute("activeEvents", eventService.countActiveEvents());
        model.addAttribute("totalEvents", eventService.countTotalEvents());

        return "dashboard/home";
    }
}