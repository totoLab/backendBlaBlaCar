package org.example.services;

import org.springframework.transaction.annotation.Transactional;
import org.example.entities.Ad;
import org.example.repositories.AdRepo;
import org.example.repositories.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Map.entry;

@Service
public class CommonServices {

    @Autowired
    BookingRepo bookingRepository;

    @Autowired
    AdRepo adRepository;

    @Autowired
    private AdServices adServices;

    @Transactional(readOnly = true)
    public Map<String, List<String>> getCities() {
        Set<String> departureCities = new TreeSet<>();
        Set<String> arrivalCities = new TreeSet<>();

        for (Ad ad : adServices.getAvailableAds()) {
            departureCities.add(ad.getDepartureCity());
            arrivalCities.add(ad.getArrivalCity());
        }
        Map<String, List<String>> cities = Map.ofEntries(
                entry("departureCities", departureCities.stream().toList()),
                entry("arrivalCities", arrivalCities.stream().toList())
        );
        return cities;
    }

}
