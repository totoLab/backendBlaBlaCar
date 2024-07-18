package org.example.controllers;

import org.example.entities.Ad;
import org.example.entities.Booking;
import org.example.entities.User;
import org.example.exceptions.BookingAlreadyExistsException;
import org.example.repositories.BookingRepo;
import org.example.services.AdServices;
import org.example.services.CommonServices;
import org.example.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ads/{id}/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    BookingRepo bookingRepository;

    @Autowired
    UserServices userServices;

    @Autowired
    AdServices adServices;

    @Autowired
    CommonServices commonServices;


    // user == admin || authenticated user == booking.user
    @GetMapping
    public ResponseEntity<?> getBookings(@PathVariable Long id) {
        User user = commonServices.getCurrentUser();
        boolean admin = commonServices.isAdmin(user);

        List<Booking> bookings = userServices.getBookings(user, id, admin);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // authenticated user != ad.publisher
    @PostMapping("/new")
    public ResponseEntity<?> makeBooking(@PathVariable Long id) {
        User user = commonServices.getCurrentUser();
        Long bookingId;
        try {
            bookingId = userServices.bookARide(user, id).getId();
        } catch (BookingAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Booking failed: " + e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(bookingId, HttpStatus.OK);
    }

    // authenticated user != ad.publisher
    @PostMapping("/cancel")
    public ResponseEntity<?> removeBooking(@PathVariable Long id) {
        User user = commonServices.getCurrentUser();

        try {
            userServices.removeBooking(user, id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Booking removal failed: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

}
