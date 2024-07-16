package org.example.repositories;

import org.example.entities.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AdRepo extends JpaRepository<Ad, Long>  {

    List<Ad> findByDepartureCityAndArrivalCityAndDateAfter(String departureCity, String arrivalCity, LocalDate date);
    List<Ad> findByDepartureCityAndArrivalCityAndDate(String departureCity, String arrivalCity, LocalDate date);
    List<Ad> findByDateAfter(LocalDate date);

    boolean existsByDepartureCityAndArrivalCityAndDateAfter(String departureCity, String arrivalCity, LocalDate date);
    boolean existsByDepartureCityAndArrivalCityAndDateAfterAndTwoBackSeats(String departureCity, String arrivalCity, LocalDate date, boolean twoBackSeats);

    boolean existsByDepartureCityAndArrivalCityAndDate(String departureCity, String arrivalCity, LocalDate date);
    boolean existsByDepartureCityAndArrivalCityAndDateAndTwoBackSeats(String departureCity, String arrivalCity, LocalDate date, boolean twoBackSeats);

}
