package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.user.CustomUserDetailsService;
import com.eventaro.Eventaro.application.user.dto.UserRequest;
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.domain.booking.BookingRepository;
import com.eventaro.Eventaro.domain.user.User;
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

        UserRequest newUserRequest = new UserRequest();
        newUserRequest.setUsername(username);
        newUserRequest.setPassword(password);
        newUserRequest.setFullName(fullName);
        newUserRequest.setRole("CUSTOMER"); // Standardmäßig neue User als "CUSTOMER" anlegen

        userService.registerUser(newUserRequest);

        return "redirect:/login?registered";
    }

    // Hilfsmethode: Leitet je nach Rolle weiter
    @GetMapping("/redirect-by-role")
    public String defaultAfterLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // --- EASTER EGG CHECK ---
        // Wenn der User die Rolle DOOMSLAYER hat, geht es zum Spiel
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOOMSLAYER"))) {
            return "redirect:/secret/doom";
        }

        // Standard Checks
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            return "redirect:/my-profile/bookings";
        }

        // Alle anderen (Admin, Backoffice, Frontoffice) gehen zum Dashboard
        return "redirect:/dashboard";
    }

    // --- KUNDENBEREICH: MEINE BUCHUNGEN ---
    @GetMapping("/my-profile/bookings")
    public String myBookings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        List<Booking> myBookings = bookingRepository.findByEmailIgnoreCase(currentUsername);

        model.addAttribute("bookings", myBookings);
        model.addAttribute("username", currentUsername);

        return "public/my-bookings";
    }
}