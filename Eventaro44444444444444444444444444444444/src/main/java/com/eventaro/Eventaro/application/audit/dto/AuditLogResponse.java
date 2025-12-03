package com.eventaro.Eventaro.application.audit.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {
    private LocalDateTime timestamp;
    private String type;
    private String message;
    private String username;

    // Getter & Setter
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // WICHTIG: Helper für das Template, da dort "log.user" aufgerufen wird
    public String getUser() { return username; }
}