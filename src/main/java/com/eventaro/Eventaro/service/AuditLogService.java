package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.AuditLog;
import com.eventaro.Eventaro.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String type, String message) {
        // Den aktuell eingeloggten User ermitteln
        String currentUsername = "System"; // Fallback
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            }
        } catch (Exception e) {
            // Ignorieren, falls kein SecurityContext da ist (z.B. beim Booten)
        }

        AuditLog logEntry = new AuditLog(type, message, currentUsername);
        auditLogRepository.save(logEntry);
    }

    // Für die Anzeige im Admin-Bereich
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}