package com.eventaro.Eventaro.application.event.mapper;

import com.eventaro.Eventaro.application.event.dto.EventRequest;
import com.eventaro.Eventaro.application.event.dto.EventResponseDTO;
import com.eventaro.Eventaro.domain.event.Event;
import com.eventaro.Eventaro.enums.StatusOfEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Component
public class EventMapper {

    public EventResponseDTO toResponse(Event entity) {
        if (entity == null) return null;

        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setOrganizer(entity.getOrganizer());
        dto.setPrice(entity.getPrice());
        dto.setCapacity(entity.getCapacity());
        dto.setCategory(entity.getCategory());
        dto.setSkillLevel(entity.getSkillLevel());
        dto.setDescription(entity.getDescription());
        dto.setRequirements(entity.getRequirements());
        dto.setEquipment(entity.getEquipment());
        dto.setStatus(entity.getStatus());
        dto.setDates(entity.getDates());
        dto.setAvailableAddOns(entity.getAvailableAddOns());

        return dto;
    }

    public Event toEntity(EventRequest request) {
        if (request == null) return null;

        // ID ist null (wird generiert), Status ist DRAFT (Standard)
        Event event = new Event(
                null,
                request.getTitle(),
                request.getOrganizer(),
                request.getCategory(),
                request.getSkillLevel(),
                request.getPrice(),
                request.getCapacity() != null ? request.getCapacity() : 10,
                StatusOfEvent.DRAFT
        );
        event.setDescription(request.getDescription());
        event.setRequirements(request.getRequirements());
        event.setEquipment(request.getEquipment());

        // --- DATUMS LOGIK ---
        if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
            try {
                // 1. Start-Zeitpunkt parsen
                LocalDate startDate = LocalDate.parse(request.getStartDate());
                LocalTime time = (request.getStartTime() != null && !request.getStartTime().isEmpty())
                        ? LocalTime.parse(request.getStartTime())
                        : LocalTime.of(10, 0); // Default 10 Uhr

                LocalDateTime firstEventDate = LocalDateTime.of(startDate, time);
                event.addDate(firstEventDate); // Der erste Termin ist immer dabei

                // 2. Serien-Logik (Recurring Events)
                if (request.isRecurring() && request.getRepeatUntil() != null && !request.getRepeatUntil().isEmpty()) {
                    LocalDate repeatEnd = LocalDate.parse(request.getRepeatUntil());

                    LocalDateTime nextDate = firstEventDate;
                    int safetyLimit = 104; // Sicherheits-Limit (z.B. max 2 Jahre wöchentlich), um Endlosschleifen zu verhindern
                    int counter = 0;

                    while (counter < safetyLimit) {
                        // Nächsten Termin berechnen basierend auf Zyklus
                        if ("daily".equalsIgnoreCase(request.getCycle())) {
                            nextDate = nextDate.plusDays(1);
                        } else if ("weekly".equalsIgnoreCase(request.getCycle())) {
                            nextDate = nextDate.plusWeeks(1);
                        } else if ("monthly".equalsIgnoreCase(request.getCycle())) {
                            nextDate = nextDate.plusMonths(1);
                        } else {
                            break; // Unbekannter Zyklus -> Abbruch
                        }

                        // Prüfen ob Enddatum überschritten
                        if (nextDate.toLocalDate().isAfter(repeatEnd)) {
                            break;
                        }

                        // Termin hinzufügen
                        event.addDate(nextDate);
                        counter++;
                    }
                }

            } catch (DateTimeParseException e) {
                System.err.println("Fehler beim Parsen des Datums: " + request.getStartDate() + " oder " + request.getRepeatUntil());
            }
        } else {
            // Fallback Dummy-Datum, falls gar nichts eingegeben wurde
            event.addDate(LocalDateTime.now().plusDays(14).withHour(10).withMinute(0));
        }

        return event;
    }

    public void updateEntityFromRequest(Event entity, EventRequest request) {
        entity.setTitle(request.getTitle());
        entity.setOrganizer(request.getOrganizer());
        entity.setPrice(request.getPrice());
        entity.setCategory(request.getCategory());
        entity.setSkillLevel(request.getSkillLevel());
        entity.setDescription(request.getDescription());
        entity.setRequirements(request.getRequirements());
        entity.setEquipment(request.getEquipment());
        if (request.getCapacity() != null) entity.setCapacity(request.getCapacity());

        // Hinweis: Ein Update von bestehenden Serien ist komplex (Löschen? Neu generieren?).
        // Das lassen wir hier sicherheitshalber aus, damit keine Buchungen zerschossen werden.
    }
}