package com.eventaro.Eventaro.application.event.dto;

import com.eventaro.Eventaro.enums.StatusOfEvent;
import com.eventaro.Eventaro.domain.event.AddOn;
import java.time.LocalDateTime;
import java.util.List;

public class EventResponseDTO {
    private Long id;
    private String title;
    private String organizer;
    private Double price;
    private Integer capacity;
    private String category;
    private String skillLevel;
    private String description;
    private String requirements;
    private String equipment;
    private StatusOfEvent status;

    // Wir geben hier direkt die Liste zurück, das Template formatiert sie
    private List<LocalDateTime> dates;
    private List<AddOn> availableAddOns;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public StatusOfEvent getStatus() { return status; }
    public void setStatus(StatusOfEvent status) { this.status = status; }
    public List<LocalDateTime> getDates() { return dates; }
    public void setDates(List<LocalDateTime> dates) { this.dates = dates; }
    public List<AddOn> getAvailableAddOns() { return availableAddOns; }
    public void setAvailableAddOns(List<AddOn> availableAddOns) { this.availableAddOns = availableAddOns; }
}