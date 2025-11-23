package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.enums.EventStatus;
import com.eventaro.Eventaro.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/") // Root URL für die Startseite
public class PublicEventController {

    private final EventService eventService;

    public PublicEventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Die Startseite für Kunden: Zeigt nur veröffentlichte Events an
    @GetMapping
    public String publicEventList(Model model) {
        // Wir filtern hier nur Events, die PUBLISHED sind (Logik könnte auch in den Service wandern)
        List<Event> publicEvents = eventService.getAllEvents().stream()
                .filter(e -> e.getStatusOfEvent() == EventStatus.PUBLISHED)
                .collect(Collectors.toList());

        model.addAttribute("events", publicEvents);
        return "public/index"; // Neues Template
    }
}