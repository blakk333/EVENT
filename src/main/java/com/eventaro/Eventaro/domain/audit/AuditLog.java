package com.eventaro.Eventaro.domain.audit;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String type;     // z.B. CREATE, UPDATE, DELETE, LOGIN
    private String message;  // Was wurde gemacht?
    private String username; // Wer hat es gemacht?

    // Leerer Konstruktor für JPA
    public AuditLog() {}

    public AuditLog(String type, String message, String username) {
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.message = message;
        this.username = username;
    }

    // Getter
    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getUsername() { return username; }

    // WICHTIG für das Template: Das Template ruft "log.user" auf.
    // Wir leiten das einfach an getUsername() weiter.
    public String getUser() { return username; }
}