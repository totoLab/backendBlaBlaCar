package org.example.services;

import org.example.exceptions.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.repositories.*;
import org.example.entities.*;

@Service
public class UserServices {

    @Autowired
    BookingRepo bookingRepository;

    @Autowired
    AdRepo adRepository;

    @Autowired
    UserRepo userRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, BookingAlreadyExistsException.class, UserNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public Booking bookARide(User user, Ad ad) throws NoSeatsLeftException, AdNotFoundException, UserNotFoundException {
        if (user != null && userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            if (ad != null && adRepository.existsByDepartureCityAndArrivalCityAndDate(ad.getDepartureCity(), ad.getArrivalCity(), ad.getDate())) { // TODO add two back seats handling
                if (ad.getBooked_seats() <= 0) throw new NoSeatsLeftException("Non sono rimasti posti per questo annuncio");
                ad.setBooked_seats(ad.getBooked_seats() - 1);

                Booking booking = new Booking();
                booking.setBooker(user);
                userRepository.save(user);
                booking.setAd(ad);
                return bookingRepository.save(booking);
            } else {
                throw new AdNotFoundException("Annuncio " + ad + " non trovato");
            }
        } else {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, BookingAlreadyExistsException.class, UserNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public void removeBooking(User user, Ad ad) throws AdNotFoundException, UserNotFoundException, BookingNotFoundException {
        if (user != null) {
            if (ad != null) {
                Booking booking = bookingRepository.findByBookerAndAd(user, ad);
                bookingRepository.delete(booking);
                ad.setBooked_seats(ad.getBooked_seats() + 1);
            } else {
                throw new AdNotFoundException("Annuncio " + ad + " non trovato");
            }
        } else {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, UserNotFoundException.class})
    public void addAd(User user, Ad ad) throws Exception {
        if (user != null && userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            if (ad != null && ad.getMax_seats() > 0) {
                if (!adRepository.existsById(ad.getId())) adRepository.save(ad);
                else throw new Exception("Annuncio " + ad + " già esistente ");
            } else {
                throw new Exception("Invalid ad entity: " + ad);
            }
        } else {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, UserNotFoundException.class})
    public void removeAd(User user, Ad ad) throws Exception {
        if (user != null && userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            if (ad != null && ad.getPublisher().equals(user)) {
                if (adRepository.existsById(ad.getId())) adRepository.delete(ad);
                else throw new AdNotFoundException("Annuncio " + ad + " non trovato");
            } else {
                throw new Exception("Invalid ad entity: " + ad);
            }
        } else {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }
    }

}
