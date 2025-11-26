package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.Booking;
import com.eventaro.Eventaro.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Findet Buchungen für den heutigen Tag (Tagesgeschäft)
    public List<Booking> getTodaysBookings() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return bookingRepository.findByEventDateTimeBetween(start, end);
    }

    @Transactional
    public void addBooking(Booking booking) {
        bookingRepository.save(booking);
    }

    public Optional<Booking> findByNumber(String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber);
    }

    @Transactional
    public void saveFeedback(String bookingNumber, Integer rating, String comment) {
        findByNumber(bookingNumber).ifPresent(b -> {
            b.setRating(rating);
            b.setFeedbackComment(comment);
            bookingRepository.save(b); // Speichern nicht vergessen bei JPA!
        });
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    // Dashboard Statistiken
    public long countTodaysBookings() {
        return getTodaysBookings().size();
    }

    public long countPendingCheckIns() {
        return bookingRepository.findByCheckedInFalseAndCancelledFalse().size();
    }
}