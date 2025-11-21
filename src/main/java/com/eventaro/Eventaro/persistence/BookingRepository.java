package com.eventaro.Eventaro.persistence;

import com.eventaro.Eventaro.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Findet Buchungen, bei denen das EVENT in einem bestimmten Zeitraum startet
    @Query("SELECT b FROM Booking b JOIN FETCH b.event e WHERE e.startDateTime BETWEEN :start AND :end ORDER BY e.startDateTime ASC")
    List<Booking> findBookingsForEventDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}