package org.example.services;

import org.example.exceptions.*;
import jakarta.persistence.LockModeType;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.repositories.*;
import org.example.entities.*;

import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServices {

    @Autowired
    BookingRepo bookingRepository;

    @Autowired
    AdRepo adRepository;

    @Autowired
    AdServices adServices;

    @Autowired
    KeycloakService keycloakService;

    @Transactional(readOnly = true)
    public User getUser(String username) {
        UserRepresentation userRepresentation = keycloakService.getUserByUsername(username);
        if (userRepresentation == null) {
            return null;
        }

        User user = new User();
        user.setId(userRepresentation.getId());
        user.setUsername(userRepresentation.getUsername());
        user.setEmail(userRepresentation.getEmail());
        user.setName(userRepresentation.getFirstName());
        user.setSurname(userRepresentation.getLastName());

        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (UserRepresentation userRepresentation: keycloakService.getUsers()) {
            User user = new User();
            user.setId(userRepresentation.getId());
            user.setUsername(userRepresentation.getUsername());
            user.setEmail(userRepresentation.getEmail());
            user.setName(userRepresentation.getFirstName());
            user.setSurname(userRepresentation.getLastName());
            users.add(user);
        }
        return users;
    }

    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(Authentication connectedUser, String username) throws UserNotFoundException, UnauthorizedException {
        String userId = getUser(username).getId();
        if (!connectedUser.getName().equals(userId)) {
            throw new UnauthorizedException("Operation not permitted by user " + connectedUser.getName());
        }

        return bookingRepository.findByBookerId(connectedUser.getName());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {UserNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public void deleteUser(Authentication connectedUser, String username) throws Exception {
        String userId = getUser(username).getId();
        if (!connectedUser.getName().equals(userId)) {
            throw new UnauthorizedException("Operation not permitted for user " + connectedUser.getName());
        }

        for (Booking booking : bookingRepository.findByBookerId(connectedUser.getName())) {
            try {
                removeBooking(connectedUser, booking.getAd().getId());
            } catch (UserNotFoundException | AdNotFoundException | BookingNotFoundException e) {
            }
        }
        adRepository.deleteAll(adRepository.findAdsByPublisherId(connectedUser.getName()));

        keycloakService.deleteUser(connectedUser.getName());
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookings(Authentication connectedUser, Long adId) throws UnauthorizedException {
        Ad ad = adServices.getAdById(adId);
        List<Booking> bookings = new ArrayList<>();

        if (!connectedUser.getName().equals(ad.getPublisherId())) {
            throw new UnauthorizedException("Operation not permitted for user " + connectedUser.getName());
        }

        bookings = new ArrayList<>();
        Booking booking = bookingRepository.findByBookerIdAndAd(connectedUser.getName(), ad);
        if (booking != null) {
            bookings.add(booking);
        }
        return bookings;
    }

    @Transactional(readOnly = true)
    public List<Ad> getUserAds( Authentication connectedUser, String username) throws UserNotFoundException, UnauthorizedException {
        String userId = getUser(username).getId();
        if (!connectedUser.getName().equals(userId)) {
            throw new UnauthorizedException("Operation not permitted by user " + connectedUser.getName());
        }

        return adServices.getAdByPublisher(connectedUser);
    }

    @Retryable(value = {SQLClientInfoException.class, SQLException.class})
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {AdNotFoundException.class, BookingAlreadyExistsException.class, UserNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public Booking bookARide(Authentication connectedUser, Long adId) throws NoSeatsLeftException, AdNotFoundException, UserNotFoundException, BookingAlreadyExistsException {
        Ad ad = adServices.getAdById(adId);
        if (ad == null || !adRepository.existsByDepartureCityAndArrivalCityAndDate(ad.getDepartureCity(), ad.getArrivalCity(), ad.getDate())) {
            throw new AdNotFoundException("Annuncio " + ad + " non trovato");
        }

        if (connectedUser.getName().equals(ad.getPublisherId())) {
            throw new BookingAlreadyExistsException("L'utente" + connectedUser.getName() + " ha pubblicato l'annuncio, impossibile prenotare.");
        }

        if (ad.getMaxSeats() - ad.getBookedSeats() <= 0) {
            throw new NoSeatsLeftException("Non sono rimasti posti per questo annuncio");
        }

        if (bookingRepository.existsByBookerIdAndAdId(connectedUser.getName(), ad.getId()))
            throw new BookingAlreadyExistsException("Esiste già una prenotazione per l'utente " + connectedUser.getName() + " su questo annuncio (" + ad.getId() + ")");

        ad.setBookedSeats(ad.getBookedSeats() + 1);
        
        Booking booking = new Booking();
        booking.setBookerId(connectedUser.getName());
        booking.setAd(ad);
        return bookingRepository.save(booking);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {AdNotFoundException.class, UserNotFoundException.class, BookingNotFoundException.class})
    @Lock(LockModeType.OPTIMISTIC)
    public void removeBooking(Authentication connectedUser, Long adId) throws AdNotFoundException, UserNotFoundException, BookingNotFoundException, UnauthorizedException {
        Ad ad = adServices.getAdById(adId);
        if (ad == null) {
            throw new AdNotFoundException("Annuncio " + adId + " non trovato");
        }

        if (!bookingRepository.existsByBookerIdAndAdId(connectedUser.getName(), ad.getId()))
            throw new BookingNotFoundException("L'utente " + connectedUser.getName() + " non ha prenotazioni su questo annuncio.");

        Booking booking = bookingRepository.findByBookerIdAndAd(connectedUser.getName(), ad);
        if (booking == null) {
            throw new BookingNotFoundException("Prenotazione non trovata.");
        }

        ad.setBookedSeats(ad.getBookedSeats() - 1);
        adRepository.save(ad);
        bookingRepository.delete(booking);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, AdAlreadyExistsException.class, UserNotFoundException.class})
    public Long addAd(Authentication connectedUser, Ad ad) throws NoSeatsLeftException, UserNotFoundException, AdAlreadyExistsException, UnauthorizedException {
        if (ad == null || ad.getMaxSeats() <= 0) {
            throw new NoSeatsLeftException("Invalid ad entity: " + ad);
        }

        ad.setPublisherId(connectedUser.getName());

        if (adRepository.existsByDepartureCityAndArrivalCityAndDate(ad.getDepartureCity(), ad.getArrivalCity(), ad.getDate())) {
            throw new AdAlreadyExistsException("Annuncio " + ad + " già esistente ");
        }

        ad.setPublisherId(connectedUser.getName());
        adRepository.save(ad);
        return ad.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {UnauthorizedException.class, AdNotFoundException.class, UserNotFoundException.class})
    public void removeAd(Authentication connectedUser, Ad ad) throws UserNotFoundException, UnauthorizedException, AdNotFoundException {
        if (!connectedUser.getName().equals(ad.getPublisherId())) {
            throw new UnauthorizedException("Operation not permitted for user " + connectedUser.getName());
        }

        if (!adRepository.existsById(ad.getId())) {
            throw new AdNotFoundException("Annuncio " + ad + " non trovato");
        }

        adRepository.delete(ad);
    }

}
