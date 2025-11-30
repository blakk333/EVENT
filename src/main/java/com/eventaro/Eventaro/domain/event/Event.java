package com.eventaro.Eventaro.domain.event;

import com.eventaro.Eventaro.enums.StatusOfEvent;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String organizer;
    private String category;
    private String skillLevel;

    @Column(length = 1000) // Erlaubt längere Texte für Anforderungen
    private String requirements;

    @Column(length = 1000)
    private String equipment;

    private Double price;
    private Integer capacity;

    @Column(length = 2000) // Erlaubt längere Beschreibungen
    private String description;

    @Enumerated(EnumType.STRING)
    private StatusOfEvent status;

    // Speichert die Termine in einer separaten Tabelle "event_dates"
    @ElementCollection
    @CollectionTable(name = "event_dates", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "event_date")
    private List<LocalDateTime> dates = new ArrayList<>();

    // Speichert die AddOns in einer separaten Tabelle "event_addons"
    @ElementCollection
    @CollectionTable(name = "event_addons", joinColumns = @JoinColumn(name = "event_id"))
    private List<AddOn> availableAddOns = new ArrayList<>();

    // Leerer Konstruktor für JPA
    public Event() {}

    public Event(Long id, String title, String organizer, String category,
                 String skillLevel, Double price, Integer capacity, StatusOfEvent status) {
        this.id = id;
        this.title = title;
        this.organizer = organizer;
        this.category = category;
        this.skillLevel = skillLevel;
        this.price = price;
        this.capacity = capacity;
        this.status = status;
        this.requirements = "Keine besonderen Anforderungen.";
        this.equipment = "Wetterfeste Kleidung.";
    }

    // Helfer-Methoden
    public void addDate(LocalDateTime date) { this.dates.add(date); }
    public void addAddOn(AddOn addOn) { this.availableAddOns.add(addOn); }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // Setter für JPA wichtig

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatusOfEvent getStatus() { return status; }
    public void setStatus(StatusOfEvent status) { this.status = status; }

    public List<LocalDateTime> getDates() { return dates; }
    public List<AddOn> getAvailableAddOns() { return availableAddOns; }
}