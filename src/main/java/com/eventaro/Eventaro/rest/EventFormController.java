package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.datatransfer.CreateEventRequest;
import com.eventaro.Eventaro.enums.*;
import com.eventaro.Eventaro.service.EventService;
import com.eventaro.Eventaro.persistence.CategoryRepository;
import com.eventaro.Eventaro.persistence.OrganizerRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/events")
public class EventFormController {

    private final EventService eventService;
    private final OrganizerRepository organizerRepository;
    private final CategoryRepository categoryRepository;

    public EventFormController(EventService eventService,
                               OrganizerRepository organizerRepository,
                               CategoryRepository categoryRepository) {
        this.eventService = eventService;
        this.organizerRepository = organizerRepository;
        this.categoryRepository = categoryRepository;
    }

    //  Formular anzeigen
    @GetMapping("/create")
    public String showCreateEventForm(Model model) {
        model.addAttribute("organizers", organizerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("skillLevels", SkillLevel.values());
        model.addAttribute("countries", Country.values());
        model.addAttribute("createEventRequest", new CreateEventRequest());
        return "create-event/create-event"; // Pfad zu deinem Thymeleaf-Template
    }

    // Formular absenden → Event speichern
    @PostMapping("/create")
    public String createEvent(
            @ModelAttribute("createEventRequest") @Valid CreateEventRequest request,
            BindingResult binding,   // <-- must be immediately after @Valid DTO
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {

        // ---------- Cross-field validations that should show under specific fields ----------
        // Participants
        if (request.getMinParticipants() != null && request.getMaxParticipants() != null) {
            if (request.getMaxParticipants() < request.getMinParticipants()) {
                binding.rejectValue(
                        "maxParticipants",
                        "Participants.range",
                        "Maximum number of participants must be greater or equal to the minimum number."
                );
            }
        }

        // Date & time ordering
        var sd = request.getStartDate();
        var ed = request.getEndDate();
        var st = request.getStartTime();
        var et = request.getEndTime();

        if (sd != null && ed != null) {
            if (ed.isBefore(sd)) {
                // end date before start date -> show error under End Date
                binding.rejectValue(
                        "endDate",
                        "DateRange.order",
                        "End date must be after start date."
                );
            } else if (ed.equals(sd) && st != null && et != null && !et.isAfter(st)) {
                // same day -> end time must be strictly after start time -> show under End time
                binding.rejectValue(
                        "endTime",
                        "TimeRange.order",
                        "End time must be after start time when start and end dates are the same."
                );
            }
        }

        // If any errors (from Bean Validation or our custom checks) -> show form again
        if (binding.hasErrors()) {
            model.addAttribute("organizers", organizerRepository.findAll());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("skillLevels", SkillLevel.values());
            model.addAttribute("countries", Country.values());


            return "create-event/create-event";
        }

        // ---------- Create ----------
        try {
            eventService.createEvent(request, imageFile);
            return "redirect:/events/view";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error uploading image");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating event: " + e.getMessage());
        }

        // Re-render with lists after exception
        model.addAttribute("organizers", organizerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("skillLevels", SkillLevel.values());
        model.addAttribute("countries", Country.values());
        return "create-event/create-event";
    }

}
