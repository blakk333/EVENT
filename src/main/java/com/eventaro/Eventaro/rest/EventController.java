package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.model.Booking; // NEU
import com.eventaro.Eventaro.model.Event;
import com.eventaro.Eventaro.model.StatusOfEvent;
import com.eventaro.Eventaro.repository.BookingRepository; // NEU
import com.eventaro.Eventaro.service.CategoryService;
import com.eventaro.Eventaro.service.EventService;
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
    private BookingRepository bookingRepository; // NEU: Repository für Teilnehmerliste injizieren

    @GetMapping("/view")
    public String viewEvents(Model model) {
        // Lädt Events aus der Datenbank
        model.addAttribute("events", eventService.getAllEvents());
        return "management/events-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        prepareDropdowns(model);
        return "management/create-event";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return eventService.getEventById(id).map(event -> {
            model.addAttribute("event", event);
            prepareDropdowns(model);
            return "management/edit-event";
        }).orElse("redirect:/events/view");
    }

    @PostMapping("/create")
    public String processCreateEvent(@RequestParam String title,
                                     @RequestParam String organizer,
                                     @RequestParam Double price,
                                     @RequestParam String category,
                                     @RequestParam String skillLevel,
                                     @RequestParam String description,
                                     @RequestParam(required = false) String requirements,
                                     @RequestParam(required = false) String equipment) {

        Event newEvent = new Event(null, title, organizer, category, skillLevel, price, 10, StatusOfEvent.DRAFT);
        newEvent.setDescription(description);
        if (requirements != null) newEvent.setRequirements(requirements);
        if (equipment != null) newEvent.setEquipment(equipment);

        // Dummy-Datum für Demo-Zwecke (damit es nicht leer ist)
        newEvent.addDate(LocalDateTime.now().plusDays(14).withHour(10).withMinute(0));

        eventService.saveEvent(newEvent);
        return "redirect:/events/view";
    }

    @PostMapping("/update")
    public String updateEvent(@RequestParam Long id,
                              @RequestParam String title,
                              @RequestParam String organizer,
                              @RequestParam Double price,
                              @RequestParam String category,
                              @RequestParam String skillLevel,
                              @RequestParam String description,
                              @RequestParam(required = false) String requirements,
                              @RequestParam(required = false) String equipment) {

        eventService.getEventById(id).ifPresent(event -> {
            event.setTitle(title);
            event.setOrganizer(organizer);
            event.setPrice(price);
            event.setCategory(category);
            event.setSkillLevel(skillLevel);
            event.setDescription(description);
            if (requirements != null) event.setRequirements(requirements);
            if (equipment != null) event.setEquipment(equipment);

            eventService.saveEvent(event); // Speichert Änderungen in DB
        });

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

    // --- NEU: Teilnehmerliste (WI #72) ---
    @GetMapping("/{id}/participants")
    public String participantList(@PathVariable Long id, Model model) {
        return eventService.getEventById(id).map(event -> {
            model.addAttribute("event", event);

            // Wir suchen alle Buchungen für dieses Event, die nicht storniert sind.
            // Hinweis: Wir nutzen den Titel als Verknüpfung (vereinfachtes Datenmodell)
            List<Booking> participants = bookingRepository.findByEventNameAndCancelledFalse(event.getTitle());

            model.addAttribute("participants", participants);
            model.addAttribute("now", LocalDateTime.now()); // Für das Druckdatum auf der Liste

            return "management/participant-list";
        }).orElse("redirect:/events/view");
    }

    // Hilfsmethode für Dropdowns
    private void prepareDropdowns(Model model) {
        model.addAttribute("organizers", List.of("Alpinschule Innsbruck", "Wassersport Tirol", "Mountain Guides"));

        // Kategorien dynamisch aus der Datenbank laden
        List<String> categories = categoryService.getAllCategories().stream()
                .map(cat -> cat.getName())
                .collect(Collectors.toList());

        model.addAttribute("categories", categories);
        model.addAttribute("skillLevels", List.of("Anfänger", "Fortgeschritten", "Profi"));
    }
}