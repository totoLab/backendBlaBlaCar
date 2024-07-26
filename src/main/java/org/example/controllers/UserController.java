package org.example.controllers;

import org.example.entities.User;
import lombok.*;
import org.example.exceptions.UnauthorizedException;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.exceptions.UserNotFoundException;
import org.example.services.AdServices;
import org.example.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.repositories.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    UserServices userServices;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userServices.getAllUsers());
    }

    // everyone
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username){
        User u = userServices.getUser(username);
        if (u == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    // authenticated user == user with username || user == admin
    @GetMapping("/{username}/bookings")
    public ResponseEntity<?> getUserBookings(@PathVariable String username, Authentication connectedUser){
        ResponseEntity<?> response;
        try {
            response = new ResponseEntity<>(userServices.getUserBookings(connectedUser, username), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    // authenticated user == user with username || user == admin
    @GetMapping("/{username}/ads")
    public ResponseEntity<?> getUserAds(@PathVariable String username, Authentication connectedUser){
        ResponseEntity<?> response;
        try {
            response = new ResponseEntity<>(userServices.getUserAds(connectedUser, username), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    // authenticated user == user with username || user == admin
    @PostMapping("/{username}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable String username, Authentication connectedUser){
        try {
            userServices.deleteUser(connectedUser, username);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

