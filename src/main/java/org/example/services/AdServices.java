package org.example.services;

import org.example.entities.Ad;
import org.example.exceptions.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.repositories.AdRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//? not checking for availability because server admits removing a booking
@Service
public class AdServices {

    @Autowired
    AdRepo adRepository;

    @Transactional(readOnly = true)
    public List<Ad> getAvailableAds() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return adRepository.findByDateAfter(yesterday);
    }

    @Transactional(readOnly = true)
    public Ad getAdById(long id) {
        return adRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Ad getAvailableById(long id) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return adRepository.findByIdAndDateAfter(id, yesterday);
    }

    // consider filter twoBackSeats disabled when false and filtering based on it when true
    @Transactional(readOnly = true)
    public List<Ad> getAdsByDepartureCityAndArrivalCity(String departureCity, String arrivalCity, boolean twoBackSeats) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        if (twoBackSeats) {
            if (!adRepository.existsByDepartureCityAndArrivalCityAndDateAfterAndTwoBackSeats(departureCity, arrivalCity, yesterday, true))
                return new ArrayList<>();

            return adRepository.findByDepartureCityAndArrivalCityAndDateAfter(departureCity, arrivalCity, yesterday);
        }
        if (!adRepository.existsByDepartureCityAndArrivalCityAndDateAfter(departureCity, arrivalCity, yesterday))
            return new ArrayList<>();

        return adRepository.findByDepartureCityAndArrivalCityAndDateAfter(departureCity, arrivalCity, yesterday);
    }

    @Transactional(readOnly = true)
    public List<Ad> getAdsByDepartureCityAndArrivalCityAndDate(String departureCity, String arrivalCity, LocalDate date, boolean twoBackSeats) throws InvalidDateException {
        if (!date.isAfter(LocalDate.now().minusDays(1)))
            throw new InvalidDateException("Can't search for an ad before today.");

        if (twoBackSeats) {
            if (!adRepository.existsByDepartureCityAndArrivalCityAndDateAndTwoBackSeats(departureCity, arrivalCity, date, true))
                return new ArrayList<>();

            return adRepository.findByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date);
        }
        if (!adRepository.existsByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date))
            return new ArrayList<>();

        return adRepository.findByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date);
    }



}
