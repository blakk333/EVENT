package com.eventaro.Eventaro.domain.agency;

import com.eventaro.Eventaro.domain.event.Event;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contingents")
public class Contingent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private CorporateClient client; // Wer reserviert?

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event; // Für welches Event?

    private int reservedCount; // Wie viele Plätze?
    private LocalDate expiryDate; // Bis wann geblockt?

    public Contingent() {}

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CorporateClient getClient() { return client; }
    public void setClient(CorporateClient client) { this.client = client; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public int getReservedCount() { return reservedCount; }
    public void setReservedCount(int reservedCount) { this.reservedCount = reservedCount; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}