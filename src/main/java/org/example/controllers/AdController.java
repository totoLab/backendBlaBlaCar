package org.example.controllers;

import org.example.entities.Ad;
import org.example.entities.User;
import org.example.repositories.AdRepo;
import org.example.repositories.BookingRepo;
import org.example.repositories.UserRepo;
import org.example.services.AdServices;
import org.example.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ads")
@CrossOrigin(origins = "http://localhost:4200")
public class AdController {

    @Autowired
    AdRepo adRepository;

    @Autowired
    BookingRepo bookingRepository;

    @Autowired
    UserRepo userRepository;

    @Autowired
    AdServices adServices;

    @Autowired
    UserServices userServices;

    @GetMapping
    public ResponseEntity<?> getAds() {
        List<Ad> ads = adServices.getAvailableAds();
        HttpStatus status = HttpStatus.OK;
        if (ads.isEmpty()) {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(ads, status);
    }

    // everyone if searching for ads with data >= today, otherwise only authorised users can see them: admin, publisher and bookers)
    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAd(@PathVariable Long id) {
        Ad ad = adServices.getAdById(id);
        HttpStatus status = HttpStatus.OK;
        if (ad == null) {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(ad, status);
    }

    // authenticated user == ad.publisher
    @PostMapping("/ad")
    public ResponseEntity<?> addAd(@RequestBody Ad ad){
        try {
            if (
                    ad.getDate().isAfter(LocalDate.now()) &&
                    !adRepository.existsByDepartureCityAndArrivalCityAndDateAfterAndTwoBackSeats(
                        ad.getDepartureCity(), ad.getArrivalCity(),
                        ad.getDate(), ad.isTwoBackSeats()
                    )
            ) {
                adRepository.save(ad);
                return new ResponseEntity<>(ad.getId(), HttpStatus.OK);
            }
            return new ResponseEntity<>("Ad already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // authenticated user == ad.publisher || user == admin
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> deleteAd(@PathVariable Long id) {
        Ad ad = adServices.getAdById(id);
        HttpStatus status = HttpStatus.OK;
        if (ad == null) {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }




}
