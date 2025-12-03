package com.eventaro.Eventaro.application.event.dto;

public class EventRequest {
    // Basis-Daten
    private String title;
    private String organizer;
    private Double price;
    private Integer capacity;
    private String category;
    private String skillLevel;
    private String description;
    private String requirements;
    private String equipment;

    // Datums-Logik (aus dem Formular)
    private String startDate; // "2025-12-01"
    private String startTime; // "10:00"

    // --- NEU: Felder für Serien-Events (WI #Recurring) ---
    private boolean recurring;      // Checkbox aus dem HTML
    private String cycle;           // "daily", "weekly", "monthly"
    private String repeatUntil;     // End-Datum der Serie (String vom Input type="date")

    // Getter & Setter
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
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    // --- NEU: Getter/Setter für Wiederholung ---
    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public String getRepeatUntil() { return repeatUntil; }
    public void setRepeatUntil(String repeatUntil) { this.repeatUntil = repeatUntil; }
}