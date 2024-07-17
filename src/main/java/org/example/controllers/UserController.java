package org.example.controllers;

import org.example.entities.User;
import lombok.*;
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
    UserRepo userRepository;

    @Autowired
    UserServices userServices;

    // admin only
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    // everyone
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username){
        try{
            if (userRepository.existsByUsername(username)) {
                User u = userRepository.findByUsername(username);
                return new ResponseEntity<>(u, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // user admin and everyone during sign up
    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody User user){
        try {
            if (!userRepository.existsById(user.getId())) {
                userRepository.save(user);
                return new ResponseEntity<>(user.getUsername(), HttpStatus.OK);
            }
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // authenticated user == user with username || user == admin
    @PostMapping("/{username}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        try {
            if (userRepository.existsByUsername(username)) {
                User u = userRepository.findByUsername(username);
                userRepository.delete(u);
                return new ResponseEntity<>(u, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

