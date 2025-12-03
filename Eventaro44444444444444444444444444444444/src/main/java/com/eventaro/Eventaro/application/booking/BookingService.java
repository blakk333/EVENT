package com.eventaro.Eventaro.application.booking;

import com.eventaro.Eventaro.application.booking.dto.BookingResponseDTO;
import com.eventaro.Eventaro.application.booking.mapper.BookingMapper;
import com.eventaro.Eventaro.domain.booking.Booking;
import com.eventaro.Eventaro.domain.booking.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    // --- LESE-METHODEN (Geben DTOs zurück) ---

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDTO> getTodaysBookings() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return bookingRepository.findByEventDateTimeBetween(start, end).stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    // --- SCHREIB-METHODEN ---

    @Transactional
    public void addBooking(Booking booking) {
        // Diese Methode wird z.B. vom Controller aufgerufen, nachdem er das DTO verarbeitet hat.
        // Langfristig könnte man hier auch direkt createBooking(BookingRequest) machen.
        bookingRepository.save(booking);
    }

    public Optional<Booking> findByNumber(String bookingNumber) {
        // Gibt Entity zurück, da wir darauf arbeiten (Check-In, Pay, etc.)
        return bookingRepository.findByBookingNumber(bookingNumber);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Transactional
    public void saveFeedback(String bookingNumber, Integer rating, String comment) {
        findByNumber(bookingNumber).ifPresent(b -> {
            b.setRating(rating);
            b.setFeedbackComment(comment);
            bookingRepository.save(b);
        });
    }

    // --- DASHBOARD ---

    public long countTodaysBookings() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return bookingRepository.findByEventDateTimeBetween(start, end).size();
    }

    public long countPendingCheckIns() {
        return bookingRepository.findByCheckedInFalseAndCancelledFalse().size();
    }
}