package com.eventaro.Eventaro.application.audit;

import com.eventaro.Eventaro.application.audit.dto.AuditLogResponse;
import com.eventaro.Eventaro.application.audit.mapper.AuditLogMapper;
import com.eventaro.Eventaro.domain.audit.AuditLog;
import com.eventaro.Eventaro.domain.audit.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper mapper; // <--- Mapper injizieren

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

    // Für die Anzeige im Admin-Bereich: Gibt jetzt DTOs zurück!
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}