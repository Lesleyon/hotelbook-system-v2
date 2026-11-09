package org.example.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.entities.Hotel;
import org.example.entities.Room;
import org.example.repositories.HotelRepository;
import org.example.repositories.RoomRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HotelController {
    private final HotelRepository hotelRep;
    private final RoomRepository roomRep;
    public HotelController(HotelRepository hotelRep, RoomRepository roomRep) {
        this.hotelRep = hotelRep;
        this.roomRep = roomRep;
    }

    @PostMapping("/hotels")
    public Hotel createHotel(@RequestBody Hotel hotel, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN can create hotels");
        }
        return hotelRep.save(hotel);
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public Room createRoom(@RequestBody Room room) {
        if (room.getAvailable() == null) room.setAvailable(true);
        if (room.getTimesBooked() == null) room.setTimesBooked(0);
        return roomRep.save(room);
    }

    @GetMapping("/hotels")
    public List<Hotel> getHotels() {
        return hotelRep.findAll();
    }

    @GetMapping("/rooms")
    public List<Room> getAvailableRooms() {
        return roomRep.findByAvailableTrue();
    }

    @GetMapping("/rooms/recommend")
    public List<Room> getRecommendedRooms() {
        return roomRep.findByAvailableTrueOrderByTimesBookedAscIdAsc();
    }
}