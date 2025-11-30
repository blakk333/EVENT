package com.eventaro.Eventaro.application.audit.mapper;

import com.eventaro.Eventaro.application.audit.dto.AuditLogResponse;
import com.eventaro.Eventaro.domain.audit.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(AuditLog entity) {
        if (entity == null) return null;

        AuditLogResponse dto = new AuditLogResponse();
        dto.setTimestamp(entity.getTimestamp());
        dto.setType(entity.getType());
        dto.setMessage(entity.getMessage());
        dto.setUsername(entity.getUsername());

        return dto;
    }
}