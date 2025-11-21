package com.eventaro.Eventaro.persistence; // 1. Im richtigen Paket

import com.eventaro.Eventaro.domain.model.Event; // 2. Das Modell importieren
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.eventaro.Eventaro.enums.EventStatus;
import com.eventaro.Eventaro.domain.model.Organizer;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    //                                                <Entitiytyp, Typ des Primärschlüssels>


    List<Event> findByOrganizer(Organizer organizer);
    List<Event> findByStatusOfEvent(EventStatus status);

    //Neue Methode für Detailansicht (lädt Organizer, Category, Services gleich mit)
    /*
      Lädt ein Event inkl. Organizer, Category und Services in einer Abfrage,
      um LazyLoading-Probleme zu vermeiden (FETCH JOIN).
    */

    @Query("""
           select distinct e
             from Event e
             left join fetch e.organizer
             left join fetch e.category
             left join fetch e.additionalServices
            where e.id = :id
           """)
    Optional<Event> findByIdWithRefs(@Param("id") Integer id);
}
