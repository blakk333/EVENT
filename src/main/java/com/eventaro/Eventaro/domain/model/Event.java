package com.eventaro.Eventaro.domain.model;

import com.eventaro.Eventaro.enums.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    // --- GELÖSCHT: startDateTime / endDateTime ---

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "maxParticipants", nullable = false)
    private Integer maxNumberOfParticipants;

    @Column(name = "minParticipants", nullable = false)
    private Integer minNumberOfParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "scheduleType", nullable = false)
    private ScheduleType scheduleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusEvent", nullable = false)
    private EventStatus statusOfEvent;

    @Enumerated(EnumType.STRING)
    @Column(name = "skillLevel", nullable = false)
    private SkillLevel skillLevel;

    @Column(name = "cover_image", columnDefinition = "BYTEA")
    private byte[] coverImage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="street",      column=@Column(name="loc_street")),
            @AttributeOverride(name="houseNumber", column=@Column(name="loc_house_number")),
            @AttributeOverride(name="postalCode",  column=@Column(name="loc_postal_code")),
            @AttributeOverride(name="city",        column=@Column(name="loc_city")),
            @AttributeOverride(name="country",     column=@Column(name="loc_country"))
    })
    private Address location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_services",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<AdditionalService> additionalServices;

    // --- NEU: Liste der Termine ---
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventDate> dates = new ArrayList<>();

    public Event() {}

    // Hilfsmethode
    public void addDate(EventDate date) {
        dates.add(date);
        date.setEvent(this);
    }

    // Getter und Setter
    // ... (alle anderen wie vorher) ...

    // NEU für Dates
    public List<EventDate> getDates() { return dates; }
    public void setDates(List<EventDate> dates) { this.dates = dates; }

    // WICHTIG: Löschen Sie die alten getStartDateTime/setStartDateTime und EndDateTime!

    // RESTLICHE GETTER/SETTER hier einfügen (gekürzt für Übersicht)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getMaxNumberOfParticipants() { return maxNumberOfParticipants; }
    public void setMaxNumberOfParticipants(Integer maxNumberOfParticipants) { this.maxNumberOfParticipants = maxNumberOfParticipants; }
    public Integer getMinNumberOfParticipants() { return minNumberOfParticipants; }
    public void setMinNumberOfParticipants(Integer minNumberOfParticipants) { this.minNumberOfParticipants = minNumberOfParticipants; }
    public ScheduleType getScheduleType() { return scheduleType; }
    public void setScheduleType(ScheduleType scheduleType) { this.scheduleType = scheduleType; }
    public EventStatus getStatusOfEvent() { return statusOfEvent; }
    public void setStatusOfEvent(EventStatus statusOfEvent) { this.statusOfEvent = statusOfEvent; }
    public SkillLevel getSkillLevel() { return skillLevel; }
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }
    public byte[] getCoverImage() { return coverImage; }
    public void setCoverImage(byte[] coverImage) { this.coverImage = coverImage; }
    public Address getLocation() { return location; }
    public void setLocation(Address location) { this.location = location; }
    public Organizer getOrganizer() { return organizer; }
    public void setOrganizer(Organizer organizer) { this.organizer = organizer; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<AdditionalService> getAdditionalServices() { return additionalServices; }
    public void setAdditionalServices(List<AdditionalService> additionalServices) { this.additionalServices = additionalServices; }
}