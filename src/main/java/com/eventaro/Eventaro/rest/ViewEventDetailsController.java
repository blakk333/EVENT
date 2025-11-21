package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.persistence.EventRepository;
import com.eventaro.Eventaro.service.EventService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/events")
public class ViewEventDetailsController {

    private final EventRepository repo;
    private final EventService eventService;

    public ViewEventDetailsController(EventRepository repo, EventService eventService) {
        this.repo = repo;
        this.eventService = eventService;
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Integer id, Model model) {
        Event event = eventService.getEventDetails(id); // delegiert in Service
        model.addAttribute("event", event);
        return "event-details/view-event-details";
    }


    // Bild aus BYTEA (falls vorhanden)
    @GetMapping("/details/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Integer id) {
        return repo.findById(id)
                .filter(ev -> ev.getCoverImage() != null && ev.getCoverImage().length > 0)
                .map(ev -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)   // oder IMAGE_JPEG, je nach Inhalt
                        .body(ev.getCoverImage()))
                .orElse(ResponseEntity.noContent().build());
    }
}
