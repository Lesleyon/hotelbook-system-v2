package org.example.controllers;

import org.example.entities.Room;
import org.example.repositories.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private static final Logger log = LoggerFactory.getLogger(RoomController.class);
    private final RoomRepository roomRep;
    public RoomController(RoomRepository roomRep) {
        this.roomRep = roomRep;
    }

    @PostMapping("/{id}/confirm-availability")
    public void confirmAvailability(@PathVariable Long id,
                                    @RequestParam(required = false) LocalDate startDate,
                                    @RequestParam(required = false) LocalDate endDate,
                                    @RequestParam(required = false) String requestId) {
        log.info("confirm-availability room={} requestId={}", id, requestId);
        Room room = roomRep.findById(id).orElse(null);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        if (requestId != null && requestId.equals(room.getLastRequestId())) {
            log.info("Idempotent skip confirm requestId={} room={}", requestId, id);
            return;
        }

        if (!Boolean.TRUE.equals(room.getAvailable())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room not available");
        }
        room.setTimesBooked(room.getTimesBooked() + 1);
        room.setLastRequestId(requestId);
        roomRep.save(room);
        log.info("room {} times_booked={}", id, room.getTimesBooked());
    }

    @PostMapping("/{id}/release")
    public void release(@PathVariable Long id, @RequestParam(required = false) String requestId) {
        log.info("release room={} requestId={}", id, requestId);
        Room room = roomRep.findById(id).orElse(null);
        if (room == null) return;

        if (requestId != null && requestId.equals(room.getLastReleaseRequestId())) {
            log.info("Idempotent skip release requestId={} room={}", requestId, id);
            return;
        }

        if (room.getTimesBooked() > 0) {
            room.setTimesBooked(room.getTimesBooked() - 1);
        }
        room.setLastReleaseRequestId(requestId);
        roomRep.save(room);
    }
}