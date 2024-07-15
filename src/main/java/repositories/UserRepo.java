package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import entities.User;
import java.util.List;

public interface UserRepo extends JpaRepository<User, Long>  {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByEmailOrUsername(String email, String username);

    List<User> findByName(String name);
    List<User> findByLastName(String lastName);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
