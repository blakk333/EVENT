package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.model.Booking;
import com.eventaro.Eventaro.model.User;
import com.eventaro.Eventaro.repository.BookingRepository;
import com.eventaro.Eventaro.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private BookingRepository bookingRepository;

    // Login Seite anzeigen
    @GetMapping("/login")
    public String showLoginForm() {
        return "public/login";
    }

    // Registrierung anzeigen
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "public/register";
    }

    // Registrierung verarbeiten
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String fullName) {
        // Standardmäßig neue User als "CUSTOMER" anlegen
        User newUser = new User(username, password, "CUSTOMER", fullName);
        userService.registerUser(newUser);
        return "redirect:/login?registered";
    }

    // Hilfsmethode: Leitet je nach Rolle weiter
    @GetMapping("/redirect-by-role")
    public String defaultAfterLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            return "redirect:/my-profile/bookings";
        }
        return "redirect:/dashboard";
    }

    // --- KUNDENBEREICH: MEINE BUCHUNGEN ---
    @GetMapping("/my-profile/bookings")
    public String myBookings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName(); // Das ist bei uns meist die E-Mail oder Username

        // Wir suchen Buchungen anhand der E-Mail Adresse (Username)
        // Das setzt voraus, dass User sich mit ihrer E-Mail registrieren
        List<Booking> myBookings = bookingRepository.findByEmailIgnoreCase(currentUsername);

        model.addAttribute("bookings", myBookings);
        model.addAttribute("username", currentUsername);

        return "public/my-bookings";
    }
}