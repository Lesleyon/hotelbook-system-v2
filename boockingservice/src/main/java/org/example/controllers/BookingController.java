package org.example.controllers;

import org.example.BoockingService;
import org.example.entities.Booking;
import org.example.entities.User;
import org.example.repositories.BoockingRepository;
import org.example.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
public class BookingController {
    private final BoockingService bookingService;
    private final BoockingRepository bookingRep;
    private final UserRepository UserRep;

    public BookingController(BoockingService bookingService, BoockingRepository bookingRep, UserRepository UserRep)
    {this.bookingService = bookingService;this.bookingRep = bookingRep;this.UserRep = UserRep;
    }

    @PostMapping("/booking")
    public Booking create(@RequestBody Booking booking, @RequestParam(defaultValue = "false") boolean autoSelect,
                          HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = UserRep.findByUsername(username);

        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return bookingService.createBooking(user.getId(), booking, autoSelect);
    }

    @GetMapping("/bookings")
    public List<Booking> myBookings(Authentication auth) {
        User user = UserRep.findByUsername(auth.getName());
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return bookingRep.findByUserId(user.getId());
    }

    @GetMapping("/booking/{id}")
    public Booking getById(@PathVariable Long id) {
        Booking booking = bookingRep.findById(id).orElse(null);
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return booking;
    }

    @DeleteMapping("/booking/{id}")
    public Booking cancel(@PathVariable Long id) {
        Booking booking = bookingRep.findById(id).orElse(null);
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        booking.setStatus("CANCELLED");
        return bookingRep.save(booking);
    }
}