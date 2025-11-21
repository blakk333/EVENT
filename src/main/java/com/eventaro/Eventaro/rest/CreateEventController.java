package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.datatransfer.*;
import com.eventaro.Eventaro.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events") // All endpoints using this scheme
public class CreateEventController {

    // importing the service
    private final EventService eventService;

    public CreateEventController(EventService eventService) {
        this.eventService = eventService;
    }

    //First Endpoint -> URL = POST /api/events
    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Event> createEvent(
            @Valid @RequestPart("eventData") CreateEventRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Event createdEvent = eventService.createEvent(request, imageFile);
            // if it works, status 201 should be returned
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (IOException e) {
            // this is in case the loading of the image fails
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            //in case the organizer/category could not be found
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        try {
            Event event = eventService.getEventById(id);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}