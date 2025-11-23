package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final EventService eventService;

    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    // KORREKTUR: Pfad auf "/home" geändert, um Konflikt mit PublicEventController zu lösen
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        // WICHTIG: Stellen Sie sicher, dass eine Datei 'src/main/resources/templates/index.html' existiert!
        // Falls nicht, ändern Sie dies auf eine existierende View (z.B. "view-event-list-backoffice/view-events")
        return "index";
    }
}