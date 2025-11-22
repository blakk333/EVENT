package com.eventaro.Eventaro.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_dates")
public class EventDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    // Verknüpfung zum Haupt-Event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Optional: Maximale Teilnehmerzahl pro spezifischem Termin (überschreibt ggf. Event-Standard)
    // private Integer maxParticipants;

    public EventDate() {}

    public EventDate(LocalDateTime start, LocalDateTime end, Event event) {
        this.startDateTime = start;
        this.endDateTime = end;
        this.event = event;
    }

    // Getter und Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}