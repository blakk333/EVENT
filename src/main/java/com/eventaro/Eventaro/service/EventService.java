package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.datatransfer.*;
import com.eventaro.Eventaro.domain.model.*;
import com.eventaro.Eventaro.enums.*;
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
    private final AuditLogService auditLogService;

    public EventService(EventRepository eventRepository,
                        OrganizerRepository organizerRepository,
                        CategoryRepository categoryRepository,
                        AdditionalServiceRepository additionalServiceRepository,
                        AuditLogService auditLogService) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.categoryRepository = categoryRepository;
        this.additionalServiceRepository = additionalServiceRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Event createEvent(CreateEventRequest request, MultipartFile imageFile) throws IOException {
        // 1. Organisator und Kategorie laden
        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        // 2. Adresse mappen (Hilfsmethode unten nutzen)
        Address address = mapAddress(request.getLocation());

        // 3. Event Basisdaten setzen
        Event event = new Event();
        event.setName(request.getTitle());
        event.setBasePrice(request.getPrice());
        event.setDescription(request.getDescription());
        event.setMinNumberOfParticipants(request.getMinParticipants());
        event.setMaxNumberOfParticipants(request.getMaxParticipants());
        event.setSkillLevel(request.getSkillLevel());
        event.setStatusOfEvent(EventStatus.DRAFT);
        event.setScheduleType(request.isRecurring() ? ScheduleType.RECURRING : ScheduleType.ONE_TIME);
        event.setOrganizer(organizer);
        event.setCategory(category);
        event.setLocation(address);

        // --- NEU: Loop über die Liste der Termine aus dem Generator ---
        if (request.getDates() != null) {
            for (CreateEventRequest.EventDateDTO dto : request.getDates()) {
                LocalDateTime start = LocalDateTime.of(dto.getStartDate(), dto.getStartTime());
                LocalDateTime end = LocalDateTime.of(dto.getEndDate(), dto.getEndTime());

                EventDate date = new EventDate(start, end, event);
                event.addDate(date);
            }
        }
        // --------------------------------------------------------------

        // Bild speichern
        if (imageFile != null && !imageFile.isEmpty()) {
            event.setCoverImage(imageFile.getBytes());
        }

        // Zusätzliche Services speichern
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
        auditLogService.log("CREATE_EVENT", "Created event: '" + savedEvent.getName() + "' (ID: " + savedEvent.getId() + ")");

        return savedEvent;
    }

    @Transactional
    public void updateEvent(Integer id, CreateEventRequest request, MultipartFile imageFile) throws IOException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Beziehungen updaten
        if (!event.getOrganizer().getId().equals(request.getOrganizerId())) {
            Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                    .orElseThrow(() -> new EntityNotFoundException("Organizer not found"));
            event.setOrganizer(organizer);
        }

        if (!event.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            event.setCategory(category);
        }

        // Basisdaten updaten
        event.setName(request.getTitle());
        event.setBasePrice(request.getPrice());
        event.setDescription(request.getDescription());
        event.setMinNumberOfParticipants(request.getMinParticipants());
        event.setMaxNumberOfParticipants(request.getMaxParticipants());
        event.setSkillLevel(request.getSkillLevel());
        event.setScheduleType(request.isRecurring() ? ScheduleType.RECURRING : ScheduleType.ONE_TIME);

        // Adresse updaten
        Address newAddr = mapAddress(request.getLocation());
        event.setLocation(newAddr);

        // --- NEU: Termine aktualisieren (Liste leeren und neu befüllen) ---
        event.getDates().clear(); // Alte Termine löschen
        if (request.getDates() != null) {
            for (CreateEventRequest.EventDateDTO dto : request.getDates()) {
                LocalDateTime start = LocalDateTime.of(dto.getStartDate(), dto.getStartTime());
                LocalDateTime end = LocalDateTime.of(dto.getEndDate(), dto.getEndTime());

                EventDate date = new EventDate(start, end, event);
                event.addDate(date);
            }
        }
        // ------------------------------------------------------------------

        // Bild updaten
        if (imageFile != null && !imageFile.isEmpty()) {
            event.setCoverImage(imageFile.getBytes());
        }

        eventRepository.save(event);
        auditLogService.log("UPDATE_EVENT", "Updated event: '" + event.getName() + "' (ID: " + event.getId() + ")");
    }

    // Hilfsmethode für Adress-Mapping (vermeidet doppelten Code)
    private Address mapAddress(AddressDTO loc) {
        Address address = new Address();
        address.setStreet(loc.getStreet());
        address.setCity(loc.getCity());
        address.setCountry(loc.getCountry());

        if (loc.getHouseNumber() != null && !loc.getHouseNumber().isBlank()) {
            try { address.setHousenumber(Integer.parseInt(loc.getHouseNumber().trim())); } catch (Exception e) { address.setHousenumber(0); }
        } else {
            address.setHousenumber(null);
        }

        if (loc.getPostalCode() != null && !loc.getPostalCode().isBlank()) {
            try { address.setZipCode(Integer.parseInt(loc.getPostalCode().trim())); } catch (Exception e) { address.setZipCode(0); }
        } else {
            address.setZipCode(null);
        }
        return address;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("event (id: " + id + ") not found"));
    }

    @Transactional(readOnly = true)
    public Event getEventDetails(Integer id) {
        return eventRepository.findByIdWithRefs(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }
}