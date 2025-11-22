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
    private final EventDateRepository eventDateRepository;
    private final CustomerRepository customerRepository;
    private final AuditLogService auditLogService;
    // NEU: Repo für Services
    private final AdditionalServiceRepository additionalServiceRepository;

    public BookingService(BookingRepository bookingRepository,
                          EventRepository eventRepository,
                          EventDateRepository eventDateRepository,
                          CustomerRepository customerRepository,
                          AuditLogService auditLogService,
                          AdditionalServiceRepository additionalServiceRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.eventDateRepository = eventDateRepository;
        this.customerRepository = customerRepository;
        this.auditLogService = auditLogService;
        this.additionalServiceRepository = additionalServiceRepository;
    }

    public List<Booking> getBookingsForToday() {
        LocalDate today = LocalDate.now();
        return bookingRepository.findBookingsForEventDateRange(today.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    // Hilfsmethode um einzelne Buchung zu laden (wird vom Controller genutzt)
    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found: " + id));
    }

    @Transactional
    public Booking createBooking(CreateBookingRequest request) {
        EventDate eventDate = eventDateRepository.findById(request.getEventDateId())
                .orElseThrow(() -> new EntityNotFoundException("Event date not found"));
        Event event = eventDate.getEvent();

        if (request.getTicketCount() > event.getMaxNumberOfParticipants()) {
            throw new IllegalArgumentException("Not enough tickets available!");
        }
        if (event.getStatusOfEvent() == EventStatus.CANCELED || event.getStatusOfEvent() == EventStatus.SOLD_OUT) {
            throw new IllegalStateException("Event is not bookable.");
        }

        Customer customer = findOrCreateCustomer(request);

        Booking booking = new Booking();
        booking.setEventDate(eventDate);
        booking.setCustomer(customer);
        booking.setTicketCount(request.getTicketCount());
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setStatus(BookingStatus.CONFIRMED);

        double total = event.getBasePrice() * request.getTicketCount();
        booking.setTotalPrice(total);
        booking.setBookingNumber("B-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());

        Booking savedBooking = bookingRepository.save(booking);
        auditLogService.log("CREATE_BOOKING",
                "User booked '" + event.getName() + "' on " + eventDate.getStartDateTime() +
                        " for " + savedBooking.getTicketCount() + " Pax. Ref: " + savedBooking.getBookingNumber());

        return savedBooking;
    }

    @Transactional
    public Booking createWalkInBooking(CreateBookingRequest request, Double manualPrice) {
        EventDate eventDate = eventDateRepository.findById(request.getEventDateId())
                .orElseThrow(() -> new EntityNotFoundException("Event date not found"));
        Event event = eventDate.getEvent();

        Customer customer = findOrCreateCustomer(request);

        Booking booking = new Booking();
        booking.setEventDate(eventDate);
        booking.setCustomer(customer);
        booking.setTicketCount(request.getTicketCount());
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setStatus(BookingStatus.CHECKED_IN);

        if (manualPrice != null && manualPrice >= 0) {
            booking.setTotalPrice(manualPrice);
        } else {
            booking.setTotalPrice(event.getBasePrice() * request.getTicketCount());
        }
        booking.setBookingNumber("W-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());

        Booking savedBooking = bookingRepository.save(booking);
        auditLogService.log("WALK_IN", "Walk-in: " + savedBooking.getBookingNumber());

        return savedBooking;
    }

    // NEU: Zusatzleistungen buchen
    @Transactional
    public void addAdditionalServicesToBooking(Integer bookingId, List<Integer> serviceIds) {
        Booking booking = getBookingById(bookingId);

        StringBuilder addedNames = new StringBuilder();

        for (Integer sId : serviceIds) {
            AdditionalService service = additionalServiceRepository.findById(sId)
                    .orElseThrow(() -> new EntityNotFoundException("Service not found: " + sId));

            booking.addService(service); // Fügt hinzu und erhöht Preis
            addedNames.append(service.getName()).append(", ");
        }

        bookingRepository.save(booking);

        auditLogService.log("ADD_SERVICE",
                "Added services (" + addedNames.toString() + ") to booking " + booking.getBookingNumber() +
                        ". New Total: " + booking.getTotalPrice());
    }

    @Transactional
    public void checkInGuest(Integer bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);
        auditLogService.log("CHECK_IN", "Guest checked in: " + booking.getBookingNumber());
    }

    private Customer findOrCreateCustomer(CreateBookingRequest request) {
        return customerRepository.findByEmail(request.getEmail())
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
                        if(request.getAddress().getPostalCode()!=null) addr.setZipCode(Integer.parseInt(request.getAddress().getPostalCode()));
                        if(request.getAddress().getHouseNumber()!=null) addr.setHousenumber(Integer.parseInt(request.getAddress().getHouseNumber()));
                    } catch(Exception e){
                        if(addr.getZipCode() == null) addr.setZipCode(0);
                        if(addr.getHousenumber() == null) addr.setHousenumber(0);
                    }
                    newCust.setAddress(addr);
                    return customerRepository.save(newCust);
                });
    }
}