package org.example.repositories;

import org.example.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long>  {
    Booking findByBookerIdAndAd(String userId, Ad ad);
    List<Booking> findByBookerId(String usedId);
    List<Booking> findByAd(Ad ad);

    boolean existsByBookerIdAndAdId(String userId, Long id);
}
