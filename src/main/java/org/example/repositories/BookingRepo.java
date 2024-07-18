package org.example.repositories;

import org.example.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long>  {
    Booking findByBookerAndAd(User user, Ad ad);
    List<Booking> findByBooker(User user);
    List<Booking> findByAd(Ad ad);

    boolean existsByBookerAndAdId(User user, Long id);
}
