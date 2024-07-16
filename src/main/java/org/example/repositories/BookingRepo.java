package org.example.repositories;

import org.example.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, Long>  {
    Booking findByBookerAndAd(User user, Ad ad);
}
