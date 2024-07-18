package org.example.services;

import org.example.entities.User;
import org.example.repositories.AdRepo;
import org.example.repositories.BookingRepo;
import org.example.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServices {

    @Autowired
    BookingRepo bookingRepository;

    @Autowired
    AdRepo adRepository;

    @Autowired
    UserRepo userRepository;

    public User getCurrentUser() {
        return userRepository.findByUsername("toto");
    }

    public boolean isAdmin(User user) {
        // Logic to check if the user is an admin
        return false; // Placeholder
    }
}
