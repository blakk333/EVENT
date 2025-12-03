package com.eventaro.Eventaro.domain.agency;

import com.eventaro.Eventaro.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContingentRepository extends JpaRepository<Contingent, Long> {
    List<Contingent> findByEvent(Event event);
}