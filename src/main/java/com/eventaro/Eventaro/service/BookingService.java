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

    // Methode für "Today's Bookings" Ansicht
    public List<Booking> getBookingsForToday() {
        LocalDate today = LocalDate.now();
        return bookingRepository.findBookingsForEventDateRange(today.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    // Methode zum Erstellen einer Buchung
    @Transactional
    public Booking createBooking(CreateBookingRequest request) {
        // 1. Event laden
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // 2. Validierung
        if (request.getTicketCount() > event.getMaxNumberOfParticipants()) {
            throw new IllegalArgumentException("Not enough tickets available!");
        }
        if (event.getStatusOfEvent() == EventStatus.CANCELED || event.getStatusOfEvent() == EventStatus.SOLD_OUT) {
            throw new IllegalStateException("Event is not bookable.");
        }

        // 3. Kunde finden oder erstellen
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
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
                });

        // 4. Buchung anlegen
        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setCustomer(customer);
        booking.setTicketCount(request.getTicketCount());
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setStatus(BookingStatus.CONFIRMED);

        double total = event.getBasePrice() * request.getTicketCount();
        booking.setTotalPrice(total);

        // UUID als Buchungsnummer generieren (ersten 8 Zeichen)
        booking.setBookingNumber("B-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());

        Booking savedBooking = bookingRepository.save(booking);

        // Audit Log schreiben
        auditLogService.log("CREATE_BOOKING",
                "User booked '" + savedBooking.getEvent().getName() +
                        "' for " + savedBooking.getTicketCount() + " Pax. Ref: " + savedBooking.getBookingNumber());

        return savedBooking;
    }

    // NEUE METHODE: Check-In für Front Office
    @Transactional
    public void checkInGuest(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Status ändern
        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);

        // Loggen
        auditLogService.log("CHECK_IN",
                "Guest " + booking.getCustomer().getLastName() +
                        " checked in. (Booking: " + booking.getBookingNumber() + ")");
    }
}