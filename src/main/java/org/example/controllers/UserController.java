package org.example.controllers;

import org.example.entities.User;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.repositories.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    UserRepo userRepository;

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam String username){
        try{
            User u = userRepository.findByUsername(username);
            return new ResponseEntity<>(u, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

