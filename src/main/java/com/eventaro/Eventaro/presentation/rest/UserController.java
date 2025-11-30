package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.audit.AuditLogService;
import com.eventaro.Eventaro.application.user.CustomUserDetailsService;
import com.eventaro.Eventaro.application.user.dto.UserRequest;
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
    private AuditLogService auditLogService;

    @GetMapping("/manage")
    public String listUsers(Model model) {
        // Service liefert DTOs
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserRequest());
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserRequest userRequest) {
        userService.registerUser(userRequest);

        // Audit Log schreiben
        auditLogService.log("CREATE", "Neuer Benutzer angelegt: " + userRequest.getUsername() + " (" + userRequest.getRole() + ")");

        return "redirect:/users/manage";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);

        // Audit Log schreiben
        auditLogService.log("DELETE", "Benutzer mit ID " + id + " wurde gelöscht.");

        return "redirect:/users/manage";
    }
}