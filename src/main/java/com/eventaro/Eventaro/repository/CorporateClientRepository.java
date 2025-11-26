package com.eventaro.Eventaro.repository;

import com.eventaro.Eventaro.model.CorporateClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorporateClientRepository extends JpaRepository<CorporateClient, Long> {
}