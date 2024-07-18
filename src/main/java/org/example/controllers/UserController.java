package org.example.controllers;

import org.example.entities.User;
import lombok.*;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.exceptions.UserNotFoundException;
import org.example.services.AdServices;
import org.example.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // admin only
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userServices.getUsers(), HttpStatus.OK);
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
    public ResponseEntity<?> getUserBookings(@PathVariable String username){
        ResponseEntity<?> response;
        try {
            response = new ResponseEntity<>(userServices.getUserBookings(username), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    // user admin and everyone during sign up
    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody User user){
        try {
            userServices.addUser(user);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(user.getUsername(), HttpStatus.OK);
    }

    // authenticated user == user with username || user == admin
    @PostMapping("/{username}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        try {
            userServices.deleteUser(username);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

