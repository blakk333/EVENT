package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.Event;
import com.eventaro.Eventaro.model.StatusOfEvent;
import com.eventaro.Eventaro.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getPublishedEvents() {
        return eventRepository.findByStatus(StatusOfEvent.PUBLISHED);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Transactional
    public Event saveEvent(Event event) {
        // Setze Standardwerte wenn neu
        if (event.getId() == null && event.getStatus() == null) {
            event.setStatus(StatusOfEvent.DRAFT);
        }
        return eventRepository.save(event);
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

    // Für Dashboard Statistiken
    public long countActiveEvents() {
        return eventRepository.countByStatus(StatusOfEvent.PUBLISHED);
    }

    public long countTotalEvents() {
        return eventRepository.count();
    }
}