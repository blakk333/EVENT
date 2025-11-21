package com.eventaro.Eventaro.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String username; // Wer hat es gemacht?

    @Column(nullable = false)
    private String action; // Z.B. "CREATE_EVENT", "DELETE_BOOKING"

    @Column(columnDefinition = "TEXT")
    private String details; // Z.B. "Event 'Skiing' created with ID 5"

    public AuditLog() {}

    // Konstruktor für einfaches Erstellen
    public AuditLog(String username, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.username = username;
        this.action = action;
        this.details = details;
    }

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}