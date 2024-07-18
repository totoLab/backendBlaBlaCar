package org.example.entities;

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

    @Column(name = "departure_city", nullable = false)
    private String departureCity;
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;
    @Column(name = "arrival_city", nullable = false)
    private String arrivalCity;
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "car")
    private String car;
    @Column(name = "max_seats", nullable = false)
    private int maxSeats;
    @Column(name = "booked_seats")
    private int bookedSeats;
    @Column(name = "two_back_seats")
    private boolean twoBackSeats;

    @ManyToOne(fetch = FetchType.EAGER)
    private User publisher;
}
