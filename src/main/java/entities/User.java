package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "users", schema = "blablacar")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "first_name")
    private String name;
    @Column(name = "last_name")
    private String surname;
    @Column(name = "email", unique = true)
    private String email;

}
