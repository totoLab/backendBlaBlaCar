package repositories;

import entities.Booking;
import entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, Long>  {
    BookingRepo findByBookersContains(User user);
}
