package com.eventaro.Eventaro.persistence;

import com.eventaro.Eventaro.domain.model.Event;
import com.eventaro.Eventaro.domain.model.Organizer;
import com.eventaro.Eventaro.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByOrganizer(Organizer organizer);
    List<Event> findByStatusOfEvent(EventStatus status);

    // Query angepasst: Lädt jetzt auch die "dates" (Termine) mit!
    @Query("""
           select distinct e
             from Event e
             left join fetch e.organizer
             left join fetch e.category
             left join fetch e.additionalServices
             left join fetch e.dates
            where e.id = :id
           """)
    Optional<Event> findByIdWithRefs(@Param("id") Integer id);
}