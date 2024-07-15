package repositories;

import entities.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import entities.Ad;

import java.time.LocalDate;

public interface AdRepo extends JpaRepository<Ad, Long>  {

    Ad findByDepartureCity(String departureCity);
    Ad findByArrivalCity(String arrivalCity);
    Ad findByDepartureCityAndArrivalCity(String departureCity, String arrivalCity);
    Ad findAdByTwoBackSeats(boolean twoBackSeats);
    Ad findByAnimalsAllowed(boolean animalsAllowed);
    Ad findByTwoBackSeatsAndAnimalsAllowed(boolean twoBackSeats, boolean animalsAllowed);
    Ad findByDateAndDepartureCityAndArrivalCity(LocalDate date, String departureCity, String arrivalCity);


}
