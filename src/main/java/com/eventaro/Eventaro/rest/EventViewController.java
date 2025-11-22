package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.service.EventService; // <--- DIESER IMPORT FEHLTE
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventViewController {
    private final EventService eventService;

    public EventViewController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/view")
    public String viewEvents(Model model) {
        // Events aus der DB holen
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "view-event-list-backoffice/view-events";
    }
}