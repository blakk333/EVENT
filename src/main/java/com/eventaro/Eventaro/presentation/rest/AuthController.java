package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.user.CustomUserDetailsService;
import com.eventaro.Eventaro.application.user.dto.UserRequest; // <--- WICHTIG: Import hinzugefügt
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.domain.booking.BookingRepository;
// User Entity Import brauchen wir hier eigentlich nur noch, wenn showRegisterForm "new User()" macht.
// Sauberer wäre es, auch im Formular das DTO zu nutzen (siehe unten).
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
        // Wir übergeben ein leeres Objekt für das Formular.
        // "User" ist okay, solange die Felder im Template (username, password, fullName) passen.
        // Noch sauberer wäre: model.addAttribute("user", new UserRequest());
        model.addAttribute("user", new User());
        return "public/register";
    }

    // Registrierung verarbeiten
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String fullName) {

        // FEHLERBEHEBUNG: Wir müssen ein UserRequest DTO erstellen, keine User Entity!
        UserRequest newUserRequest = new UserRequest();
        newUserRequest.setUsername(username);
        newUserRequest.setPassword(password);
        newUserRequest.setFullName(fullName);
        newUserRequest.setRole("CUSTOMER"); // Standardmäßig neue User als "CUSTOMER" anlegen

        // Jetzt übergeben wir das DTO an den Service
        userService.registerUser(newUserRequest);

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