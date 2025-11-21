package com.eventaro.Eventaro.persistence;

import com.eventaro.Eventaro.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    // Die neuesten Einträge zuerst
    List<AuditLog> findAllByOrderByTimestampDesc();
}