package com.eventaro.Eventaro.domain.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Findet eine exakte Buchung anhand der Buchungsnummer (z.B. "B-1005").
     * Wichtig für: Check-In, Check-Out, Suche.
     */
    Optional<Booking> findByBookingNumber(String bookingNumber);

    /**
     * Findet alle Buchungen in einem bestimmten Zeitraum.
     * Wichtig für: Die Ansicht "Tagesgeschäft" (heute 00:00 bis 23:59).
     */
    List<Booking> findByEventDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByEmailIgnoreCase(String email);

    /**
     * Sucht Buchungen anhand des Gastnamens (Teilsuche, Groß-/Kleinschreibung egal).
     * Wichtig für: Die Suchleiste im Front Office ("Müller" findet "Markus Müller").
     */
    List<Booking> findByGuestNameContainingIgnoreCase(String guestName);


    /**
     * Findet alle Gäste, die aktuell eingecheckt (anwesend), aber noch nicht ausgecheckt sind.
     * Wichtig für: Übersicht der aktuell anwesenden Personen.
     */
    List<Booking> findByCheckedInTrueAndCheckedOutFalse();

    /**
     * Findet alle noch offenen Check-Ins (noch nicht da).
     * Wichtig für: "Pending Check-Ins" Statistik auf dem Dashboard.
     */
    List<Booking> findByCheckedInFalseAndCancelledFalse();

    // Füge diese Methode hinzu:
    List<Booking> findByEventNameAndCancelledFalse(String eventName);
}

