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

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServices {

    @Autowired
    BookingRepo bookingRepository;

    @Autowired
    AdRepo adRepository;

    @Autowired
    UserRepo userRepository;

    @Autowired
    AdServices adServices;

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUser(String username) {
        if (userRepository.existsByUsername(username)) {
            return userRepository.findByUsername(username);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(String username) throws UserNotFoundException {
        if (!userRepository.existsByUsername(username)) throw new UserNotFoundException("Utente " + username + " non trovato.");
        User user = userRepository.findByUsername(username);
        return bookingRepository.findByBooker(user);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, BookingAlreadyExistsException.class, UserNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public void addUser(User user) throws UserAlreadyExistsException {
        if (userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) throw new UserAlreadyExistsException("Utente " + user.getUsername() + " è già presente.");

        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, BookingAlreadyExistsException.class, UserNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public void deleteUser(String username) throws UserNotFoundException {
        if (!userRepository.existsByUsername(username)) throw new UserNotFoundException("Utente " + username + " non trovato.");
        User user = userRepository.findByUsername(username);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookings(User user, Long adId, boolean isAdmin) {
        Ad ad = adServices.getAdById(adId);
        List<Booking> bookings;
        if (isAdmin) {
            bookings = bookingRepository.findByAd(ad);
        } else {
            bookings = new ArrayList<>();
            Booking booking = bookingRepository.findByBookerAndAd(user, ad);
            if (booking != null) {
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, BookingAlreadyExistsException.class, UserNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public Booking bookARide(User user, Long adId) throws NoSeatsLeftException, AdNotFoundException, UserNotFoundException, BookingAlreadyExistsException {
        if (user == null || !userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }

        Ad ad = adServices.getAdById(adId);
        if (ad == null || !adRepository.existsByDepartureCityAndArrivalCityAndDate(ad.getDepartureCity(), ad.getArrivalCity(), ad.getDate())) {
            throw new AdNotFoundException("Annuncio " + ad + " non trovato");
        }

        if (ad.getBookedSeats() <= 0) {
            throw new NoSeatsLeftException("Non sono rimasti posti per questo annuncio");
        }

        if (bookingRepository.existsByBookerAndAdId(user, ad.getId()))
            throw new BookingAlreadyExistsException("Esiste già una prenotazione per questo utente su questo annuncio");

        ad.setBookedSeats(ad.getBookedSeats() + 1);
        adRepository.save(ad);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setAd(ad);
        return bookingRepository.save(booking);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, UserNotFoundException.class, BookingNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public void removeBooking(User user, Long adId) throws AdNotFoundException, UserNotFoundException, BookingNotFoundException {
        if (user == null || !userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }

        Ad ad = adServices.getAdById(adId);
        if (ad == null) {
            throw new AdNotFoundException("Annuncio " + ad + " non trovato");
        }

        if (!bookingRepository.existsByBookerAndAdId(user, ad.getId()))
            throw new BookingNotFoundException("L'utente " + user.getUsername() + " non ha prenotazioni su questo annuncio.");

        Booking booking = bookingRepository.findByBookerAndAd(user, ad);
        if (booking == null) {
            throw new BookingNotFoundException("Prenotazione non trovata.");
        }

        bookingRepository.delete(booking);
        ad.setBookedSeats(ad.getBookedSeats() - 1);
        adRepository.save(ad);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, UserNotFoundException.class})
    public Long addAd(User user, Ad ad) throws Exception {
        if (user == null || !userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }

        if (ad == null || ad.getMaxSeats() <= 0) {
            throw new Exception("Invalid ad entity: " + ad);
        }

        if (adRepository.existsById(ad.getId())) {
            throw new Exception("Annuncio " + ad + " già esistente ");
        }

        User u = userRepository.findByUsername(user.getUsername());
        ad.setPublisher(u);
        adRepository.save(ad);
        return ad.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdNotFoundException.class, UserNotFoundException.class})
    public void removeAd(User user, Ad ad) throws Exception {
        if (user == null || ad == null) {
            throw new Exception("Invalid entities passed.");
        }

        if (!userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new UserNotFoundException("Utente " + user + " non trovato.");
        }

        User u = userRepository.findByEmailOrUsername(user.getEmail(), user.getUsername());
        if (!ad.getPublisher().equals(u)) {
            throw new Exception("Utente non autorizzato alla rimozione");
        }

        if (!adRepository.existsById(ad.getId())) {
            throw new AdNotFoundException("Annuncio " + ad + " non trovato");
        }

        adRepository.delete(ad);
    }

}
