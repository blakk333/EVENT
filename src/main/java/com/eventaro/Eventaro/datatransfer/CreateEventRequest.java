package com.eventaro.Eventaro.datatransfer;

import com.eventaro.Eventaro.enums.SkillLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateEventRequest {

    // Title of the Event
    @NotBlank(message = "Title must not be empty!")
    @Size(min = 3, message = "Title has to be at least 3 characters long.")
    private String title;

    // Organizer (required)
    @NotNull(message = "Please choose an organizer.")
    private Integer organizerId;

    // Category (required)
    @NotNull(message = "Please select a category.")
    private Integer categoryId;

    // Min participants
    @NotNull(message = "Please define minimum number of participants.")
    @Min(value = 1, message = "Minimum number has to be greater or equal to one.")
    private Integer minParticipants;

    // Max participants
    @NotNull(message = "Please define maximum number of participants.")
    @Min(value = 1, message = "Maximum number must be at least one.")
    private Integer maxParticipants;

    // Skill level
    @NotNull(message = "Please select a Skill-level.")
    private SkillLevel skillLevel;

    // Location (structured, for @Embeddable Address)
    @Valid
    @NotNull(message = "Address must not be empty.")
    private AddressDTO location = new AddressDTO();

    // Dates
    @NotNull(message = "Start Date must not be empty.")
    @FutureOrPresent(message = "Start Date has to be today or in the future.")
    private LocalDate startDate;

    @NotNull(message = "End Date must not be empty.")
    @FutureOrPresent(message = "End Date has to be today or in the future.")
    private LocalDate endDate;

    // Times
    @NotNull(message = "Please choose a starting time.")
    private LocalTime startTime;

    @NotNull(message = "Please choose when the event is over.")
    private LocalTime endTime;

    // Recurring
    private boolean recurring;

    // Price
    @NotNull(message = "Price must be set.")
    @PositiveOrZero(message = "We are not giving away money — price has to be positive or zero.")
    private Double price;

    // Description
    private String description;

    // Additional packages
    @Valid
    private java.util.List<DTOAdditionalServices> additionalPackages;

    // ---------------- small logical validations ----------------

    // Max participants must be >= min participants
    @AssertTrue(message = "Maximum number of participants has to be greater or equal to the minimum number.")
    private boolean isParticipantRangeValid() {
        if (this.minParticipants == null || this.maxParticipants == null) return true;
        return this.maxParticipants >= this.minParticipants;
    }

    // End must be strictly after start (considers date + time)
    @AssertTrue(message = "Event must end after it starts (end date/time must be after start date/time).")
    private boolean isDateTimeRangeValid() {
        if (startDate == null || endDate == null || startTime == null || endTime == null) return true;
        var start = startDate.atTime(startTime);
        var end   = endDate.atTime(endTime);
        return end.isAfter(start);
    }

    // ---------------- getters / setters ----------------

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

    public java.util.List<DTOAdditionalServices> getAdditionalPackages() { return additionalPackages; }
    public void setAdditionalPackages(java.util.List<DTOAdditionalServices> additionalPackages) { this.additionalPackages = additionalPackages; }
}
