package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ads", schema = "blablacar")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "departure_city")
    private String departureCity;
    @Column(name = "departure_time")
    private LocalTime departureTime;
    @Column(name = "arrival_city")
    private String arrivalCity;
    @Column(name = "arrival_time")
    private LocalTime arrivalTime;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "car")
    private String car;
    @Column(name = "max_seats")
    private int max_seats;
    @Column(name = "two_back_seats")
    private boolean twoBackSeats;
    @Column(name = "animals_allowed")
    private boolean animalsAllowed;

    @ManyToOne(fetch = FetchType.LAZY)
    private User publisher;
}
