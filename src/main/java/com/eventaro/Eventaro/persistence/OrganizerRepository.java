package com.eventaro.Eventaro.persistence;

import com.eventaro.Eventaro.domain.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Integer> {

}