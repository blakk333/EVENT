package com.eventaro.Eventaro.application.category.dto;

public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String icon;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}