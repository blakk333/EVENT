package com.eventaro.Eventaro.repository;

import com.eventaro.Eventaro.model.Contingent;
import com.eventaro.Eventaro.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContingentRepository extends JpaRepository<Contingent, Long> {
    List<Contingent> findByEvent(Event event);
}