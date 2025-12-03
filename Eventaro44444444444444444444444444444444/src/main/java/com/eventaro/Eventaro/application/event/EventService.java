package com.eventaro.Eventaro.application.event;

import com.eventaro.Eventaro.application.event.dto.EventRequest;
import com.eventaro.Eventaro.application.event.dto.EventResponseDTO;
import com.eventaro.Eventaro.application.event.mapper.EventMapper;
import com.eventaro.Eventaro.domain.event.Event;
import com.eventaro.Eventaro.domain.event.EventRepository;
// WICHTIG: StatusOfEvent liegt in domain.event, nicht in enums (laut deiner Dateistruktur)
import com.eventaro.Eventaro.enums.StatusOfEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponseDTO> getPublishedEvents() {
        return eventRepository.findByStatus(StatusOfEvent.PUBLISHED).stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Gibt Optional<ResponseDTO> zurück für Controller-Verwendung
    public Optional<EventResponseDTO> getEventById(Long id) {
        return eventRepository.findById(id).map(eventMapper::toResponse);
    }

    // Interne Methode, falls man doch mal die Entity braucht (z.B. für Buchungen)
    public Optional<Event> findEventEntityById(Long id) {
        return eventRepository.findById(id);
    }

    @Transactional
    public void createEvent(EventRequest request) {
        // Hier greift jetzt die neue Logik im Mapper:
        // Wenn "Recurring" ausgewählt wurde, generiert der Mapper automatisch die Termin-Liste.
        Event event = eventMapper.toEntity(request);
        eventRepository.save(event);
    }

    @Transactional
    public void updateEvent(Long id, EventRequest request) {
        eventRepository.findById(id).ifPresent(event -> {
            eventMapper.updateEntityFromRequest(event, request);
            eventRepository.save(event);
        });
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, StatusOfEvent status) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setStatus(status);
            eventRepository.save(event);
        });
    }

    // Dashboard Statistiken
    public long countActiveEvents() {
        return eventRepository.countByStatus(StatusOfEvent.PUBLISHED);
    }

    public long countTotalEvents() {
        return eventRepository.count();
    }
}