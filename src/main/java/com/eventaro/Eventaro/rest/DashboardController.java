package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.service.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final EventService eventService;

    public DashboardController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 1. Den eingeloggten Usernamen holen
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2. Daten für das Template bereitstellen
        model.addAttribute("username", username);
        model.addAttribute("events", eventService.getAllEvents());

        // 3. Wir nutzen die Event-Liste als Dashboard-Ansicht
        return "view-event-list-backoffice/view-events";
    }
}