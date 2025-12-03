package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.booking.BookingService;
import com.eventaro.Eventaro.application.booking.PriceCalculationService;
import com.eventaro.Eventaro.application.booking.dto.BookingResponseDTO;
import com.eventaro.Eventaro.application.invoice.InvoiceService;
import com.eventaro.Eventaro.application.invoice.dto.InvoiceResponseDTO;
import com.eventaro.Eventaro.application.notification.EmailService;
import com.eventaro.Eventaro.application.notification.dto.NotificationDTO;
import com.eventaro.Eventaro.application.notification.mapper.NotificationMapper;
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.enums.BookingType;
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

    @Autowired
    private NotificationMapper notificationMapper; // NEU: Mapper für E-Mails

    @GetMapping("/today")
    public String dailyBookings(Model model) {
        model.addAttribute("todayDate", LocalDate.now());
        // Service liefert jetzt DTOs, das Template zeigt diese an
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

        // Entity erstellen (für die DB)
        Booking newBooking = new Booking(bookingNum, guestName, email, eventName,
                LocalDateTime.now(), BookingType.INDIVIDUAL, 1, 50.00);
        newBooking.setCheckedIn(true);

        bookingService.addBooking(newBooking);

        // NEU: E-Mail via DTO versenden
        NotificationDTO notif = notificationMapper.toDto(newBooking);
        emailService.sendBookingConfirmation(notif);

        return "redirect:/bookings/today";
    }

    @PostMapping("/checkin")
    public String checkIn(@RequestParam String bookingNumber) {
        // findByNumber liefert Entity, damit wir setCheckedIn aufrufen können
        bookingService.findByNumber(bookingNumber).ifPresent(b -> {
            b.setCheckedIn(true);
            bookingService.addBooking(b);
        });
        return "redirect:/bookings/today";
    }

    @PostMapping("/deposit")
    public String makeDeposit(@RequestParam String bookingNumber,
                              @RequestParam(required = false) Double amount,
                              RedirectAttributes redirectAttributes) {

        if (amount == null || amount <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fehler: Bitte geben Sie einen gültigen Betrag für die Anzahlung ein.");
            return "redirect:/bookings/today";
        }

        bookingService.findByNumber(bookingNumber).ifPresent(b -> {
            double current = b.getDepositAmount();
            b.setDepositAmount(current + amount);
            bookingService.addBooking(b);
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

            // Rechnung erstellen (Service liefert jetzt InvoiceResponseDTO)
            InvoiceResponseDTO invoice = invoiceService.createInvoiceFromBooking(booking);
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
        // Service liefert DTOs
        List<BookingResponseDTO> list = bookingService.getAllBookings();
        model.addAttribute("bookings", list);

        // Berechnung angepasst auf DTO-Felder
        double totalRevenue = list.stream()
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice() : 0.0)
                .sum();

        long pending = list.stream()
                .filter(b -> !b.isCheckedIn() && !b.isCancelled())
                .count();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingCheckIns", pending);
        model.addAttribute("totalBookingsCount", list.size());

        return "management/bookings-list";
    }

    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        // findById liefert Entity für die Logik
        bookingService.findById(id).ifPresent(booking -> {
            double fee = priceCalculationService.calculateCancellationFee(booking, LocalDate.now());

            booking.setCancelled(true);
            bookingService.addBooking(booking);

            // Notification DTO erstellen (inkl. Stornogebühr)
            NotificationDTO notif = notificationMapper.toDto(booking, fee);
            emailService.sendCancellationNotification(notif); // Methode im EmailService angepasst

            redirectAttributes.addFlashAttribute("successMessage",
                    "Buchung " + booking.getBookingNumber() + " storniert. E-Mail an Gast versendet. Gebühr: € " + String.format("%.2f", fee));
        });
        return "redirect:/bookings/all";
    }
}