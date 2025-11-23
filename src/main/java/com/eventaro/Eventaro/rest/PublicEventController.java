package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/") // Hört auf Root (Startseite) UND /events/list
public class PublicEventController {

    private final EventService eventService;

    public PublicEventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Startseite
    @GetMapping
    public String publicEventList(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "public-events"; // Verweist auf das neue Template oben
    }

    // Alias, falls man explizit /events/list aufruft
    @GetMapping("/events/list")
    public String publicEventListAlias(Model model) {
        return publicEventList(model);
    }
}