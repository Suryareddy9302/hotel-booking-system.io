package com.example.hotelbooking.controller;

import com.example.hotelbooking.model.Booking;
import com.example.hotelbooking.model.Room;
import com.example.hotelbooking.repository.BookingRepository;
import com.example.hotelbooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Allows your frontend to connect
public class BookingController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Handles GET requests to fetch all available rooms.
     * Mapped to HTTP GET /api/rooms/available
     * @return A list of Room objects that are available.
     */
    @GetMapping("/rooms/available")
    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsAvailable(true);
    }

    /**
     * Handles POST requests to create a new booking.
     * Mapped to HTTP POST /api/bookings
     * @param booking The booking details sent from the frontend.
     * @return A response entity containing the saved booking or a bad request error.
     */
    @PostMapping("/bookings")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        // Find the specific room the user wants to book
        Room roomToBook = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Error: Room not found for id " + booking.getRoom().getId()));

        // Check if the room is actually available before booking
        if (!roomToBook.isAvailable()) {
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request if room is already taken
        }

        // If available, mark the room as unavailable
        roomToBook.setAvailable(false);
        roomRepository.save(roomToBook);

        // Save the new booking record to the database
        Booking savedBooking = bookingRepository.save(booking);

        // Return 200 OK with the details of the created booking
        return ResponseEntity.ok(savedBooking);
    }

    /**
     * Handles GET requests to fetch all existing bookings.
     * Mapped to HTTP GET /api/bookings
     * @return A list of all Booking objects.
     */
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}