package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.datatransfer.CreateBookingRequest;
import com.eventaro.Eventaro.domain.model.*;
import com.eventaro.Eventaro.enums.BookingStatus;
import com.eventaro.Eventaro.enums.EventStatus;
import com.eventaro.Eventaro.persistence.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final CustomerRepository customerRepository;
    private final AuditLogService auditLogService;

    public BookingService(BookingRepository bookingRepository,
                          EventRepository eventRepository,
                          CustomerRepository customerRepository,
                          AuditLogService auditLogService) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.customerRepository = customerRepository;
        this.auditLogService = auditLogService;
    }

    public List<Booking> getBookingsForToday() {
        LocalDate today = LocalDate.now();
        return bookingRepository.findBookingsForEventDateRange(today.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    @Transactional
    public Booking createBooking(CreateBookingRequest request) {
        // 1. Event laden
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // --- NEU: Einen konkreten Termin finden ---
        // Da das Frontend noch keine Terminauswahl hat, nehmen wir den ersten Termin der Liste.
        if (event.getDates().isEmpty()) {
            throw new IllegalStateException("Event has no dates configured!");
        }
        // In Zukunft: request.getEventDateId() nutzen
        EventDate targetDate = event.getDates().get(0);
        // ------------------------------------------

        // 2. Validierung
        if (request.getTicketCount() > event.getMaxNumberOfParticipants()) {
            throw new IllegalArgumentException("Not enough tickets available!");
        }
        if (event.getStatusOfEvent() == EventStatus.CANCELED || event.getStatusOfEvent() == EventStatus.SOLD_OUT) {
            throw new IllegalStateException("Event is not bookable.");
        }

        // 3. Kunde finden oder erstellen
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseGet(() -> createNewCustomer(request));

        // 4. Buchung anlegen
        Booking booking = new Booking();
        booking.setEventDate(targetDate); // <--- WICHTIG: Setzt das Datum, nicht das Event
        booking.setCustomer(customer);
        booking.setTicketCount(request.getTicketCount());
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setStatus(BookingStatus.CONFIRMED);

        double total = event.getBasePrice() * request.getTicketCount();
        booking.setTotalPrice(total);

        booking.setBookingNumber("B-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());

        Booking savedBooking = bookingRepository.save(booking);

        auditLogService.log("CREATE_BOOKING",
                "User booked '" + savedBooking.getEventDate().getEvent().getName() +
                        "' for " + savedBooking.getTicketCount() + " Pax. Ref: " + savedBooking.getBookingNumber());

        return savedBooking;
    }

    @Transactional
    public void checkInGuest(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);

        auditLogService.log("CHECK_IN",
                "Guest " + booking.getCustomer().getLastName() + " checked in.");
    }

    // Helper zum Erstellen des Kunden
    private Customer createNewCustomer(CreateBookingRequest request) {
        Customer newCust = new Customer();
        newCust.setEmail(request.getEmail());
        newCust.setFirstName(request.getFirstName());
        newCust.setLastName(request.getLastName());
        newCust.setPhoneNumber(request.getPhoneNumber());

        Address addr = new Address();
        addr.setStreet(request.getAddress().getStreet());
        addr.setCity(request.getAddress().getCity());
        addr.setCountry(request.getAddress().getCountry());

        try {
            addr.setZipCode(Integer.parseInt(request.getAddress().getPostalCode()));
            addr.setHousenumber(Integer.parseInt(request.getAddress().getHouseNumber()));
        } catch (NumberFormatException e) {
            addr.setZipCode(0);
            addr.setHousenumber(0);
        }
        newCust.setAddress(addr);

        return customerRepository.save(newCust);
    }
}