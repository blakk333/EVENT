package com.eventaro.Eventaro.repository;

import com.eventaro.Eventaro.model.Event;
import com.eventaro.Eventaro.model.StatusOfEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Finde alle Events, die nicht abgesagt sind (für die Public Seite)
    List<Event> findByStatus(StatusOfEvent status);

    // Suche für das Dashboard (z.B. alle aktiven)
    long countByStatus(StatusOfEvent status);
    // NEU: Filtern nach Kategorie
    List<Event> findByStatusAndCategory(StatusOfEvent status, String category);
}