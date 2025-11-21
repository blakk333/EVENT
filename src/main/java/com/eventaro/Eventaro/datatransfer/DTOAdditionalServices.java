package com.eventaro.Eventaro.datatransfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class DTOAdditionalServices {

    @NotBlank(message = "Package title must not be empty.")
    @Size(min = 3, message = "Package title must be at least 3 characters long.")
    private String title;

    @NotBlank(message = "Description must not be empty.")
    private String description;

    @NotNull(message = "Price must be set.")
    @PositiveOrZero(message = "Package price must not be negative.")
    private Double price;

    // getters / setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
