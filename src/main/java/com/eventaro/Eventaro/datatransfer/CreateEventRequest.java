package com.eventaro.Eventaro.datatransfer;

import com.eventaro.Eventaro.enums.SkillLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CreateEventRequest {

    @NotBlank(message = "Title must not be empty!")
    @Size(min = 3, message = "Title has to be at least 3 characters long.")
    private String title;

    @NotNull(message = "Please choose an organizer.")
    private Integer organizerId;

    @NotNull(message = "Please select a category.")
    private Integer categoryId;

    @NotNull(message = "Please define minimum number of participants.")
    @Min(value = 1, message = "Minimum number has to be greater or equal to one.")
    private Integer minParticipants;

    @NotNull(message = "Please define maximum number of participants.")
    @Min(value = 1, message = "Maximum number must be at least one.")
    private Integer maxParticipants;

    @NotNull(message = "Please select a Skill-level.")
    private SkillLevel skillLevel;

    @Valid
    @NotNull(message = "Address must not be empty.")
    private AddressDTO location = new AddressDTO();

    // --- WIEDER HINZUGEFÜGT FÜR DAS FORMULAR (Kompatibilität) ---
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // Kann null sein, wenn dates-Liste genutzt wird

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;
    // --------------------------------------------

    private boolean recurring;

    @NotNull(message = "Price must be set.")
    @PositiveOrZero(message = "We are not giving away money — price has to be positive or zero.")
    private Double price;

    private String description;

    @Valid
    private List<DTOAdditionalServices> additionalPackages;

    // Liste der Termine
    @Valid
    private List<EventDateDTO> dates = new ArrayList<>();

    // ---------------- Validierung ----------------
    @AssertTrue(message = "Maximum number of participants has to be greater or equal to the minimum number.")
    private boolean isParticipantRangeValid() {
        if (this.minParticipants == null || this.maxParticipants == null) return true;
        return this.maxParticipants >= this.minParticipants;
    }

    // Getter / Setter für die Hauptklasse
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getOrganizerId() { return organizerId; }
    public void setOrganizerId(Integer organizerId) { this.organizerId = organizerId; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getMinParticipants() { return minParticipants; }
    public void setMinParticipants(Integer minParticipants) { this.minParticipants = minParticipants; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public SkillLevel getSkillLevel() { return skillLevel; }
    public void setSkillLevel(SkillLevel skillLevel) { this.skillLevel = skillLevel; }
    public AddressDTO getLocation() { return location; }
    public void setLocation(AddressDTO location) { this.location = location; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<DTOAdditionalServices> getAdditionalPackages() { return additionalPackages; }
    public void setAdditionalPackages(List<DTOAdditionalServices> additionalPackages) { this.additionalPackages = additionalPackages; }
    public List<EventDateDTO> getDates() { return dates; }
    public void setDates(List<EventDateDTO> dates) { this.dates = dates; }

    // --- HIER WAR DER FEHLER: Die innere Klasse braucht Getter UND Setter ---
    public static class EventDateDTO {
        @NotNull(message = "Start Date required")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @NotNull(message = "End Date required")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @NotNull(message = "Start Time required")
        private LocalTime startTime;

        @NotNull(message = "End Time required")
        private LocalTime endTime;

        // --- DIESE METHODEN HABEN GEFEHLT: ---
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    }
}