package org.example.controllers;

import lombok.Getter;
import org.example.entities.Ad;
import org.example.entities.User;
import org.example.exceptions.InvalidDateException;

import org.example.services.AdServices;
import org.example.services.CommonServices;
import org.example.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ads")
@CrossOrigin(origins = "http://localhost:4200")
public class AdController {

    @Autowired
    AdServices adServices;

    @Autowired
    UserServices userServices;

    @Autowired
    CommonServices commonServices;

    @GetMapping
    public ResponseEntity<?> getAds() {
        List<Ad> ads = adServices.getAvailableAds();
        HttpStatus status = HttpStatus.OK;
        if (ads.isEmpty()) {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(ads, status);
    }

    @PostMapping
    public ResponseEntity<?> getAds(@RequestBody AdQuery adQuery) {
        List<Ad> ads = List.of();
        HttpStatus status = HttpStatus.OK;

        try {
            if (adQuery.getDate() != null) {
                ads = adServices.getAdsByDepartureCityAndArrivalCityAndDate(adQuery.getDepartureCity(), adQuery.getArrivalCity(), adQuery.getDate(), adQuery.isTwoBackSeats());
            } else {
                ads = adServices.getAdsByDepartureCityAndArrivalCity(adQuery.getDepartureCity(), adQuery.getArrivalCity(), adQuery.isTwoBackSeats());
            }
        } catch (InvalidDateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if (ads.isEmpty()) {
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(ads, status);
    }

    // everyone if searching for ads with data >= today, otherwise only authorised users can see them: admin, publisher and bookers)
    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAd(@PathVariable Long id) {
        Ad ad = adServices.getAvailableById(id); // TODO, get based on permissions
        HttpStatus status = HttpStatus.OK;
        if (ad == null) {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(ad, status);
    }

    // authenticated user == ad.publisher
    @PostMapping("/ad")
    public ResponseEntity<?> addAd(@RequestBody Ad ad){
        User user = commonServices.getCurrentUser();

        Long id;
        try {
            id = userServices.addAd(user, ad);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    // authenticated user == ad.publisher || user == admin
    @PostMapping("/{id}/delete")
    public ResponseEntity<?> deleteAd(@PathVariable Long id) {
        User user = commonServices.getCurrentUser();

        Ad ad = adServices.getAdById(id);
        try {
            userServices.removeAd(user, ad);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // utils
    @GetMapping("/cities")
    public ResponseEntity<?> getCities() {
        return new ResponseEntity<>(commonServices.getCities(), HttpStatus.OK);
    }

}

@Getter
class AdQuery implements Serializable {

    private String departureCity;
    private String arrivalCity;
    private LocalDate date;
    private boolean twoBackSeats;

    AdQuery() {
        date = LocalDate.now().minusDays(1);
    }

}
