package com.eventaro.Eventaro.domain.agency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorporateClientRepository extends JpaRepository<CorporateClient, Long> {
}