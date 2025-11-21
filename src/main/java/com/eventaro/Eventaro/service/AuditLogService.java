package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.domain.model.AuditLog;
import com.eventaro.Eventaro.persistence.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Schreibt einen Eintrag ins Log.
     * Der Username wird automatisch aus dem SecurityContext geholt.
     */
    public void log(String action, String details) {
        String username = "Anonymous"; // Fallback (z.B. bei Registrierung oder System-Jobs)

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            username = auth.getName();
        }

        AuditLog logEntry = new AuditLog(username, action, details);
        auditLogRepository.save(logEntry);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}