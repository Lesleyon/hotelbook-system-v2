package org.example;

import org.example.entities.Booking;
import org.example.entities.Room;
import org.example.repositories.BoockingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BoockingService {
    private static final Logger log = LoggerFactory.getLogger(BoockingService.class);
    private final BoockingRepository bookingRep;
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8082";

    public BoockingService(BoockingRepository bookingRep, RestTemplateBuilder builder) {
        this.bookingRep = bookingRep;
        this.restTemplate = builder.connectTimeout(Duration.ofSeconds(2)).readTimeout(Duration.ofSeconds(3)).build();
    }

    private List<Room> getRecommendedRooms() {
        return restTemplate.exchange(baseUrl + "/api/rooms/recommend",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Room>>() {}
        ).getBody();
    }

    private void confirmAvailability(Long roomId, LocalDate startDate, LocalDate endDate, String requestId) {
        String url = baseUrl + "/api/rooms/" + roomId + "/confirm-availability"
                + "?startDate=" + startDate
                + "&endDate=" + endDate
                + "&requestId=" + requestId;
        restTemplate.postForEntity(url, HttpEntity.EMPTY, Void.class);
    }

    private void release(Long roomId, String requestId) {
        String url = baseUrl + "/api/rooms/" + roomId + "/release?requestId=" + requestId;
        restTemplate.postForEntity(url, HttpEntity.EMPTY, Void.class);
    }


    @Transactional
    public Booking createBooking(Long userId, Booking incoming, boolean autoSelect) {

        final String bookId;
        if (incoming.getRequestId() != null) {
            bookId = incoming.getRequestId();
        } else {
            bookId = UUID.randomUUID().toString();
        }
        log.info("[book={}] start createBooking userId={}", bookId, userId);

        if (incoming.getRequestId() != null) {
            Booking existing = bookingRep.findByRequestId(incoming.getRequestId());
            if (existing != null) {
                log.info("[bookr={}] idempotent hit bookingId={}", bookId, existing.getId());
                return existing;
            }
        }

        Long roomId = incoming.getRoomId();
        if (autoSelect) {
            List<Room> rooms = getRecommendedRooms();
            if (rooms == null || rooms.isEmpty()) {
                log.warn("[book={}] no rooms available for autoSelect", bookId);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "No rooms available");
            }
            roomId = rooms.get(0).getId();
            log.info("[book={}] autoSelect picked roomId={}", bookId, roomId);
        }
        if (roomId == null) {
            log.warn("[book={}] roomId required", bookId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomId required");
        }

        List<Booking> conflicts = bookingRep.findConflicting(roomId, incoming.getStartDate(), incoming.getEndDate());
        if (!conflicts.isEmpty()) {
            log.warn("[book={}] conflict for room={} dates {}..{}", bookId, roomId, incoming.getStartDate(),
                    incoming.getEndDate());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room already booked for these dates");
        }

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setRoomId(roomId);
        booking.setStartDate(incoming.getStartDate());
        booking.setEndDate(incoming.getEndDate());
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setRequestId(incoming.getRequestId());
        booking = bookingRep.save(booking);
        log.info("[book={}] bookingId={} PENDING room={}", bookId, booking.getId(), roomId);
        boolean confirmed = false;
        int attempts = 0;
        while (attempts < 3) {
            try {
                confirmAvailability(roomId, incoming.getStartDate(), incoming.getEndDate(), bookId);
                confirmed = true;
                break;
            } catch (Exception e) {
                attempts++;
                log.warn("[book={}] confirm attempt {} failed: {}", bookId, attempts, e.getMessage());
                if (attempts < 3) {
                    try {
                        Thread.sleep(500L * attempts);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (confirmed) {
            booking.setStatus("CONFIRMED");
            bookingRep.save(booking);
            log.info("[book={}] bookingId={} CONFIRMED", bookId, booking.getId());
        } else {
            log.error("[book={}] bookingId={} confirmation FAILED after 3 attempts, compensating",
                    bookId, booking.getId());
            booking.setStatus("CANCELLED");
            bookingRep.save(booking);
            try {
                release(roomId, bookId);
                log.info("[book={}] compensation release OK room={}", bookId, roomId);
            } catch (Exception e) {
                log.error("[book={}] compensation release FAILED room={}", bookId, roomId, e);
            }
            log.info("[book={}] bookingId={} CANCELLED, compensation finished", bookId, booking.getId());
        }
        return booking;
    }
}