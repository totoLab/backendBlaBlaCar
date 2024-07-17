package org.example.services;

import org.example.entities.Ad;
import org.example.exceptions.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.repositories.AdRepo;

import java.time.LocalDate;
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
    public List<Ad> getAdsByDepartureCityAndArrivalCity(String departureCity, String arrivalCity, boolean twoBackSeats) throws AdNotFoundException {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        if (twoBackSeats) {
            if (!adRepository.existsByDepartureCityAndArrivalCityAndDateAfterAndTwoBackSeats(departureCity, arrivalCity, yesterday, true))
                throw new AdNotFoundException("Annuncio con" +
                        " città di partenza " + departureCity +
                        " città di arrivo " + arrivalCity +
                        " e solo 2 sedili posteriori" +
                        " non trovato.");
            return adRepository.findByDepartureCityAndArrivalCityAndDateAfter(departureCity, arrivalCity, yesterday);
        }
        if (!adRepository.existsByDepartureCityAndArrivalCityAndDateAfter(departureCity, arrivalCity, yesterday))
            throw new AdNotFoundException("Annuncio con" +
                    " città di partenza " + departureCity +
                    " città di arrivo " + arrivalCity +
                    " non trovato.");
        return adRepository.findByDepartureCityAndArrivalCityAndDateAfter(departureCity, arrivalCity, yesterday);
    }

    @Transactional(readOnly = true)
    public List<Ad> getAdsByDepartureCityAndArrivalCityAndDate(String departureCity, String arrivalCity, LocalDate date, boolean twoBackSeats) throws AdNotFoundException, InvalidDateException {
        if (!date.isAfter(LocalDate.now().minusDays(1)))
            throw new InvalidDateException("Can't search for an ad before today.");

        if (twoBackSeats) {
            if (!adRepository.existsByDepartureCityAndArrivalCityAndDateAndTwoBackSeats(departureCity, arrivalCity, date, true))
                throw new AdNotFoundException("Annuncio con" +
                        " città di partenza " + departureCity +
                        " città di arrivo " + arrivalCity +
                        " in data " + date +
                        " e solo 2 sedili posteriori" +
                        " non trovato.");
            return adRepository.findByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date);
        }
        if (!adRepository.existsByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date))
            throw new AdNotFoundException("Annuncio con" +
                    " città di partenza " + departureCity +
                    " città di arrivo " + arrivalCity +
                    " in data " + date +
                    " non trovato.");
        return adRepository.findByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date);
    }



}
