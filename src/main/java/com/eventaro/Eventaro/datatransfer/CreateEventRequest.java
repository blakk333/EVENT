package com.eventaro.Eventaro.datatransfer;

import com.eventaro.Eventaro.enums.SkillLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

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
    @Min(value = 1)
    private Integer minParticipants;

    @NotNull(message = "Please define maximum number of participants.")
    @Min(value = 1)
    private Integer maxParticipants;

    @NotNull(message = "Please select a Skill-level.")
    private SkillLevel skillLevel;

    @Valid
    @NotNull
    private AddressDTO location = new AddressDTO();

    // --- NEU: Liste von Terminen statt einzelner Felder ---
    @Valid
    @NotEmpty(message = "At least one date is required.")
    private List<EventDateDTO> dates = new ArrayList<>();

    // --- Löschen Sie startDate, startTime, endDate, endTime hier! ---

    private boolean recurring;

    @NotNull
    @PositiveOrZero
    private Double price;

    private String description;

    @Valid
    private List<DTOAdditionalServices> additionalPackages;

    // --- Helper Class für die Termine ---
    public static class EventDateDTO {
        @NotNull(message = "Start Date required")
        private LocalDate startDate;
        @NotNull(message = "End Date required")
        private LocalDate endDate;
        @NotNull(message = "Start Time required")
        private LocalTime startTime;
        @NotNull(message = "End Time required")
        private LocalTime endTime;

        // Getter & Setter für EventDateDTO
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    }

    // Getter & Setter für CreateEventRequest
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

    public List<EventDateDTO> getDates() { return dates; }
    public void setDates(List<EventDateDTO> dates) { this.dates = dates; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<DTOAdditionalServices> getAdditionalPackages() { return additionalPackages; }
    public void setAdditionalPackages(List<DTOAdditionalServices> additionalPackages) { this.additionalPackages = additionalPackages; }
}