package org.example.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bookings", schema = "blablacar")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    String bookerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ad_id")
    Ad ad;

}
