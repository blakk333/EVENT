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

    @GetMapping("/")
    public String home(Model model) {
        // Wir laden alle Events für die Homepage
        model.addAttribute("events", eventService.getAllEvents());
        return "index"; // Unsere neue Homepage
    }
}