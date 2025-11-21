package com.eventaro.Eventaro.persistence;

import com.eventaro.Eventaro.domain.model.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Integer> {
}