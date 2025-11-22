package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.datatransfer.CreateEventRequest;
import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.domain.model.EventDate;
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

    // 1. CREATE: Formular anzeigen
    @GetMapping("/create")
    public String showCreateEventForm(Model model) {
        populateLists(model);
        model.addAttribute("createEventRequest", new CreateEventRequest());
        model.addAttribute("editMode", false);
        return "create-event/create-event";
    }

    // 2. CREATE: Formular absenden
    @PostMapping("/create")
    public String createEvent(
            @ModelAttribute("createEventRequest") @Valid CreateEventRequest request,
            BindingResult binding,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {

        validateRequest(request, binding);

        if (binding.hasErrors()) {
            populateLists(model);
            model.addAttribute("editMode", false);
            return "create-event/create-event";
        }

        try {
            eventService.createEvent(request, imageFile);
            return "redirect:/events/view";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating event: " + e.getMessage());
            populateLists(model);
            model.addAttribute("editMode", false);
            return "create-event/create-event";
        }
    }

    // 3. EDIT: Formular anzeigen (ANGPASST FÜR LISTEN)
    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable Integer id, Model model) {
        Event event = eventService.getEventById(id);

        CreateEventRequest request = new CreateEventRequest();
        request.setTitle(event.getName());
        request.setOrganizerId(event.getOrganizer().getId());
        request.setCategoryId(event.getCategory().getId());
        request.setPrice(event.getBasePrice());
        request.setDescription(event.getDescription());
        request.setMinParticipants(event.getMinNumberOfParticipants());
        request.setMaxParticipants(event.getMaxNumberOfParticipants());
        request.setSkillLevel(event.getSkillLevel());
        request.setRecurring(event.getScheduleType() == ScheduleType.RECURRING);

        // --- NEU: Alle Termine in die Liste laden ---
        for (EventDate ed : event.getDates()) {
            CreateEventRequest.EventDateDTO dto = new CreateEventRequest.EventDateDTO();
            dto.setStartDate(ed.getStartDateTime().toLocalDate());
            dto.setStartTime(ed.getStartDateTime().toLocalTime());
            dto.setEndDate(ed.getEndDateTime().toLocalDate());
            dto.setEndTime(ed.getEndDateTime().toLocalTime());
            request.getDates().add(dto);
        }
        // --------------------------------------------

        if (event.getLocation() != null) {
            request.getLocation().setStreet(event.getLocation().getStreet());
            request.getLocation().setCity(event.getLocation().getCity());
            request.getLocation().setCountry(event.getLocation().getCountry());
            if (event.getLocation().getHousenumber() != null) {
                request.getLocation().setHouseNumber(String.valueOf(event.getLocation().getHousenumber()));
            }
            if (event.getLocation().getZipCode() != null) {
                request.getLocation().setPostalCode(String.valueOf(event.getLocation().getZipCode()));
            }
        }

        populateLists(model);
        model.addAttribute("createEventRequest", request);
        model.addAttribute("editMode", true);
        model.addAttribute("eventId", id);

        return "create-event/create-event";
    }

    // 4. EDIT: Update speichern
    @PostMapping("/edit/{id}")
    public String updateEvent(
            @PathVariable Integer id,
            @ModelAttribute("createEventRequest") @Valid CreateEventRequest request,
            BindingResult binding,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {

        validateRequest(request, binding);

        if (binding.hasErrors()) {
            populateLists(model);
            model.addAttribute("editMode", true);
            model.addAttribute("eventId", id);
            return "create-event/create-event";
        }

        try {
            eventService.updateEvent(id, request, imageFile);
            return "redirect:/events/view";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating event: " + e.getMessage());
            populateLists(model);
            model.addAttribute("editMode", true);
            model.addAttribute("eventId", id);
            return "create-event/create-event";
        }
    }

    // --- Private Hilfsmethoden ---

    private void populateLists(Model model) {
        model.addAttribute("organizers", organizerRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("skillLevels", SkillLevel.values());
        model.addAttribute("countries", Country.values());
    }

    private void validateRequest(CreateEventRequest request, BindingResult binding) {
        // Participants Validierung
        if (request.getMinParticipants() != null && request.getMaxParticipants() != null) {
            if (request.getMaxParticipants() < request.getMinParticipants()) {
                binding.rejectValue("maxParticipants", "Participants.range",
                        "Maximum number of participants must be greater or equal to the minimum number.");
            }
        }

        // Datum & Zeit Validierung (jetzt für die Liste!)
        if (request.getDates() != null) {
            for (int i = 0; i < request.getDates().size(); i++) {
                CreateEventRequest.EventDateDTO date = request.getDates().get(i);

                if (date.getStartDate() != null && date.getEndDate() != null) {
                    if (date.getEndDate().isBefore(date.getStartDate())) {
                        // Fehler an das spezifische Listen-Element hängen
                        binding.rejectValue("dates[" + i + "].endDate", "DateRange.order",
                                "End date must be after start date.");
                    } else if (date.getEndDate().equals(date.getStartDate())
                            && date.getStartTime() != null && date.getEndTime() != null) {

                        if (!date.getEndTime().isAfter(date.getStartTime())) {
                            binding.rejectValue("dates[" + i + "].endTime", "TimeRange.order",
                                    "End time must be after start time.");
                        }
                    }
                }
            }
        }
    }
}