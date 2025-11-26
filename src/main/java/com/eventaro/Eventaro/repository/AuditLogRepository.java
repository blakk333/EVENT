package com.eventaro.Eventaro.repository;

import com.eventaro.Eventaro.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Wir wollen die Logs chronologisch sortiert sehen (neueste oben)
    List<AuditLog> findAllByOrderByTimestampDesc();
}