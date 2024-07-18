package org.example.controllers;

import org.example.entities.Ad;
import org.example.entities.Booking;
import org.example.entities.User;
import org.example.repositories.BookingRepo;
import org.example.services.AdServices;
import org.example.services.CommonServices;
import org.example.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

        Ad ad = adServices.getAdById(id);
        List<Booking> bookings;
        if (admin) {
            bookings = bookingRepository.findByAd(ad);
        } else {
            bookings = new ArrayList<>();
            Booking booking = bookingRepository.findByBookerAndAd(user, ad);
            if (booking != null) {
                bookings.add(booking);
            }
        }
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    // authenticated user != ad.publisher
    @PostMapping("/new")
    public ResponseEntity<?> bookAd(@PathVariable Long id) {
        User user = commonServices.getCurrentUser();

        if (bookingRepository.existsByBookerAndAdId(user, id)) {
            return new ResponseEntity<>("Booking already exists", HttpStatus.CONFLICT);
        }

        Ad ad = adServices.getAdById(id);
        try {
            Booking booking = userServices.bookARide(user, ad);
            return new ResponseEntity<>(booking.getId(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Booking failed: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    // authenticated user != ad.publisher
    @PostMapping("/cancel")
    public ResponseEntity<?> removeBooking(@PathVariable Long id) {
        User user = commonServices.getCurrentUser();

        if (!bookingRepository.existsByBookerAndAdId(user, id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Ad ad = adServices.getAdById(id);
        try {
            userServices.removeBooking(user, ad);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Booking removal failed: " + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

}
