package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.domain.model.*;
import com.eventaro.Eventaro.enums.*;
import com.eventaro.Eventaro.datatransfer.*;
import com.eventaro.Eventaro.persistence.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final CategoryRepository categoryRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final AuditLogService auditLogService; // <--- NEU: Service für Logs

    public EventService(EventRepository eventRepository,
                        OrganizerRepository organizerRepository,
                        CategoryRepository categoryRepository,
                        AdditionalServiceRepository additionalServiceRepository,
                        AuditLogService auditLogService) { // <--- NEU: Im Konstruktor
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.categoryRepository = categoryRepository;
        this.additionalServiceRepository = additionalServiceRepository;
        this.auditLogService = auditLogService; // <--- NEU: Zuweisung
    }

    @Transactional
    public Event createEvent(CreateEventRequest request, MultipartFile imageFile) throws IOException {
        //  Lookups
        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        //  Address mapping
        AddressDTO loc = request.getLocation();
        Address address = new Address();
        address.setStreet(loc.getStreet());
        address.setCity(loc.getCity());
        address.setCountry(loc.getCountry());

        if (loc.getHouseNumber() != null && !loc.getHouseNumber().isBlank()) {
            address.setHousenumber(parseIntOrThrow(loc.getHouseNumber().trim(), "House number must be numeric"));
        } else {
            address.setHousenumber(null);
        }

        if (loc.getPostalCode() != null && !loc.getPostalCode().isBlank()) {
            address.setZipCode(parseIntOrThrow(loc.getPostalCode().trim(), "ZIP code must be numeric"));
        } else {
            address.setZipCode(null);
        }

        //  Event erstellen
        Event event = new Event();
        event.setName(request.getTitle());
        event.setBasePrice(request.getPrice());
        event.setDescription(request.getDescription());
        event.setMinNumberOfParticipants(request.getMinParticipants());
        event.setMaxNumberOfParticipants(request.getMaxParticipants());
        event.setSkillLevel(request.getSkillLevel());
        event.setStatusOfEvent(EventStatus.DRAFT);
        event.setStartDateTime(LocalDateTime.of(request.getStartDate(), request.getStartTime()));
        event.setEndDateTime(LocalDateTime.of(request.getEndDate(), request.getEndTime()));
        event.setScheduleType(request.isRecurring() ? ScheduleType.RECURRING : ScheduleType.ONE_TIME);
        event.setOrganizer(organizer);
        event.setCategory(category);
        event.setLocation(address);

        //  Bild
        if (imageFile != null && !imageFile.isEmpty()) {
            event.setCoverImage(imageFile.getBytes());
        }

        //  Additional Services
        if (request.getAdditionalPackages() != null && !request.getAdditionalPackages().isEmpty()) {
            List<AdditionalService> services = new ArrayList<>();
            for (DTOAdditionalServices pkg : request.getAdditionalPackages()) {
                AdditionalService s = new AdditionalService();
                s.setName(pkg.getTitle());
                s.setDescription(pkg.getDescription());
                s.setPrice(pkg.getPrice());
                services.add(additionalServiceRepository.save(s));
            }
            event.setAdditionalServices(services);
        }

        Event savedEvent = eventRepository.save(event);

        // <--- NEU: Audit Log Eintrag schreiben --->
        auditLogService.log("CREATE_EVENT", "Created event: '" + savedEvent.getName() + "' (ID: " + savedEvent.getId() + ")");

        return savedEvent;
    }

    // alle Events
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Event nach ID
    public Event getEventById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("event (id: " + id + ") not found"));
    }

    //  Helper
    private Integer parseIntOrThrow(String value, String messageIfFail) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(messageIfFail);
        }
    }

    // Event-Details
    @Transactional(readOnly = true)
    public Event getEventDetails(Integer id) {
        return eventRepository.findByIdWithRefs(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }
}