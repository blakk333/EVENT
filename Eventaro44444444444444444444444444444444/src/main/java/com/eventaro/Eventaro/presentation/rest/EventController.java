package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.category.CategoryService;
import com.eventaro.Eventaro.application.event.EventService;
import com.eventaro.Eventaro.application.event.dto.EventRequest;
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.domain.booking.BookingRepository;
import com.eventaro.Eventaro.enums.StatusOfEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/view")
    public String viewEvents(Model model) {
        // Liste von DTOs
        model.addAttribute("events", eventService.getAllEvents());
        return "management/events-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        prepareDropdowns(model);
        // Leeres Request-Objekt für das Formular
        model.addAttribute("eventRequest", new EventRequest());
        return "management/create-event";
    }

    // ACHTUNG: processCreateEvent nimmt jetzt ein DTO (@ModelAttribute)
    @PostMapping("/create")
    public String processCreateEvent(@ModelAttribute EventRequest eventRequest) {
        eventService.createEvent(eventRequest);
        return "redirect:/events/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return eventService.getEventById(id).map(eventDto -> {
            model.addAttribute("event", eventDto);
            prepareDropdowns(model);
            return "management/edit-event";
        }).orElse("redirect:/events/view");
    }

    @PostMapping("/update")
    public String updateEvent(@RequestParam Long id, @ModelAttribute EventRequest eventRequest) {
        eventService.updateEvent(id, eventRequest);
        return "redirect:/events/view";
    }

    @PostMapping("/cancel")
    public String cancelEvent(@RequestParam Long eventId) {
        eventService.updateStatus(eventId, StatusOfEvent.CANCELED);
        return "redirect:/events/view";
    }

    @PostMapping("/soldout")
    public String markSoldOut(@RequestParam Long eventId) {
        eventService.updateStatus(eventId, StatusOfEvent.SOLD_OUT);
        return "redirect:/events/view";
    }

    @GetMapping("/{id}/participants")
    public String participantList(@PathVariable Long id, Model model) {
        return eventService.getEventById(id).map(event -> {
            model.addAttribute("event", event);
            // Für die Suche nach Buchungen brauchen wir den Titel
            List<Booking> participants = bookingRepository.findByEventNameAndCancelledFalse(event.getTitle());
            model.addAttribute("participants", participants);
            model.addAttribute("now", LocalDateTime.now());
            return "management/participant-list";
        }).orElse("redirect:/events/view");
    }

    private void prepareDropdowns(Model model) {
        model.addAttribute("organizers", List.of("Alpinschule Innsbruck", "Wassersport Tirol", "Mountain Guides"));
        List<String> categories = categoryService.getAllCategories().stream()
                .map(cat -> cat.getName()) // .getName() funktioniert auf DTOs genauso
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        model.addAttribute("skillLevels", List.of("Anfänger", "Fortgeschritten", "Profi"));
    }
}