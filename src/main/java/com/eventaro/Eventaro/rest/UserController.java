package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.model.User;
import com.eventaro.Eventaro.service.AuditLogService; // <--- NEU
import com.eventaro.Eventaro.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private AuditLogService auditLogService; // <--- NEU: Service injizieren

    @GetMapping("/manage")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        userService.registerUser(user);

        // --- LOGGING AUFRUF ---
        auditLogService.log("CREATE", "Neuer Benutzer angelegt: " + user.getUsername() + " (" + user.getRole() + ")");

        return "redirect:/users/manage";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        // Optional: User vor dem Löschen laden, um den Namen zu loggen
        // String username = userService.getUserById(id).getUsername(); ...

        userService.deleteUser(id);

        // --- LOGGING AUFRUF ---
        auditLogService.log("DELETE", "Benutzer mit ID " + id + " wurde gelöscht.");

        return "redirect:/users/manage";
    }
}