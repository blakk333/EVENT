package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/bookings")
public class BookingViewController {

    private final BookingService bookingService;

    public BookingViewController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/today")
    public String viewTodaysBookings(Model model) {
        model.addAttribute("bookings", bookingService.getBookingsForToday());
        model.addAttribute("todayDate", LocalDate.now());
        return "bookings/view-todays-bookings";
    }
}