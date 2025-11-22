package com.eventaro.Eventaro.persistence;

import com.eventaro.Eventaro.domain.model.EventDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDateRepository extends JpaRepository<EventDate, Integer> {
    // Finde alle Termine für ein bestimmtes Event
    List<EventDate> findByEventId(Integer eventId);
}