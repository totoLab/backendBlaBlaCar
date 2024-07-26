package org.example.services;

import org.example.entities.Ad;
import org.example.entities.Booking;
import org.example.entities.User;
import org.example.exceptions.*;
import org.example.repositories.BookingRepo;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private BookingRepo bookingRepository;

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
    public Ad getAvailableById(long id, Authentication connectedUser) {
        if (!adRepository.existsById(id)) return null;
        Ad ad = adRepository.findById(id);
        List<String> bookers = bookingRepository.findByAd(ad).stream().map(Booking::getBookerId).toList();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        if (
                ad.getPublisherId().equals(connectedUser.getName()) ||
                        bookers.contains(connectedUser.getName()) ||
                        ad.getDate().isAfter(yesterday)
        ) {
            return ad;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Ad> getAdByPublisher(Authentication connectedUser) {
        return adRepository.findAdsByPublisherId(connectedUser.getName());
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
