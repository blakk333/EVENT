package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.model.Booking;
import com.eventaro.Eventaro.model.BookingType;
import com.eventaro.Eventaro.model.Invoice;
import com.eventaro.Eventaro.service.BookingService;
import com.eventaro.Eventaro.service.EmailService;
import com.eventaro.Eventaro.service.InvoiceService;
import com.eventaro.Eventaro.service.PriceCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/today")
    public String dailyBookings(Model model) {
        model.addAttribute("todayDate", LocalDate.now());
        model.addAttribute("bookings", bookingService.getTodaysBookings());
        return "operational/daily-bookings";
    }

    @GetMapping("/walk-in")
    public String showWalkInForm(Model model) {
        model.addAttribute("events", List.of("Canyoning Basic", "Yoga im Park", "Kletterkurs", "Nachtwanderung"));
        return "operational/walk-in-form";
    }

    @PostMapping("/walk-in")
    public String processWalkIn(@RequestParam String guestName,
                                @RequestParam String email,
                                @RequestParam String eventName) {
        String bookingNum = "W-" + (1000 + new Random().nextInt(9000));
        Booking newBooking = new Booking(bookingNum, guestName, email, eventName,
                LocalDateTime.now(), BookingType.INDIVIDUAL, 1, 50.00);
        newBooking.setCheckedIn(true);
        bookingService.addBooking(newBooking);
        emailService.sendBookingConfirmation(newBooking);
        return "redirect:/bookings/today";
    }

    @PostMapping("/checkin")
    public String checkIn(@RequestParam String bookingNumber) {
        bookingService.findByNumber(bookingNumber).ifPresent(b -> {
            b.setCheckedIn(true);
            bookingService.addBooking(b);
        });
        return "redirect:/bookings/today";
    }

    // --- NEU: Anzahlung erfassen (WI #69) ---
    @PostMapping("/deposit")
    public String makeDeposit(@RequestParam String bookingNumber,
                              @RequestParam(required = false) Double amount, // WICHTIG: required = false
                              RedirectAttributes redirectAttributes) {       // WICHTIG: Für die Fehlermeldung

        // 1. Validierung: Ist der Betrag leer oder ungültig?
        if (amount == null || amount <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fehler: Bitte geben Sie einen gültigen Betrag für die Anzahlung ein.");
            return "redirect:/bookings/today";
        }

        // 2. Logik ausführen
        bookingService.findByNumber(bookingNumber).ifPresent(b -> {
            double current = b.getDepositAmount();
            b.setDepositAmount(current + amount);
            bookingService.addBooking(b);

            // Optional: Erfolgsmeldung
            redirectAttributes.addFlashAttribute("successMessage", "Anzahlung von € " + amount + " erfolgreich verbucht.");
        });

        return "redirect:/bookings/today";
    }

    @PostMapping("/pay")
    public String payBooking(@RequestParam String bookingNumber) {
        bookingService.findByNumber(bookingNumber).ifPresent(b -> {
            b.setPaid(true);
            bookingService.addBooking(b);
        });
        return "redirect:/bookings/today";
    }

    @PostMapping("/checkout")
    public String checkOut(@RequestParam String bookingNumber) {
        return bookingService.findByNumber(bookingNumber).map(booking -> {
            booking.setCheckedOut(true);
            booking.setInvoiceGenerated(true);
            bookingService.addBooking(booking);

            Invoice invoice = invoiceService.createInvoiceFromBooking(booking);
            return "redirect:/invoices/view/" + invoice.getId();
        }).orElse("redirect:/bookings/today");
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam String bookingNumber,
                                 @RequestParam Integer rating,
                                 @RequestParam String comment,
                                 @RequestParam String redirectInvoiceId) {
        bookingService.saveFeedback(bookingNumber, rating, comment);
        return "redirect:/invoices/view/" + redirectInvoiceId;
    }

    @GetMapping("/all")
    public String allBookings(Model model) {
        List<Booking> list = bookingService.getAllBookings();
        model.addAttribute("bookings", list);
        double totalRevenue = list.stream()
                .mapToDouble(b -> b.getStandardPricePerPerson() * b.getParticipantCount())
                .sum();
        long pending = list.stream().filter(b -> !b.isCheckedIn()).count();
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingCheckIns", pending);
        model.addAttribute("totalBookingsCount", list.size());
        return "management/bookings-list";
    }

    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        bookingService.findById(id).ifPresent(booking -> {
            double fee = priceCalculationService.calculateCancellationFee(booking, LocalDate.now());
            booking.setCancelled(true);
            bookingService.addBooking(booking);
            emailService.sendCancellationNotification(booking, fee);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Buchung " + booking.getBookingNumber() + " storniert. E-Mail an Gast versendet. Gebühr: € " + String.format("%.2f", fee));
        });
        return "redirect:/bookings/all";
    }
}