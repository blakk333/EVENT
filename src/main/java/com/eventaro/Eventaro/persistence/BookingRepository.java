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

    // GEÄNDERT: Navigation über eventDate (b.eventDate.startDateTime)
    @Query("SELECT b FROM Booking b JOIN FETCH b.eventDate ed JOIN FETCH ed.event e WHERE ed.startDateTime BETWEEN :start AND :end ORDER BY ed.startDateTime ASC")
    List<Booking> findBookingsForEventDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}