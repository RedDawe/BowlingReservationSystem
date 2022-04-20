package cz.reddawe.bowlingreservationsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User u WHERE u.username = ?1")
    Optional<User> findByUsername(String username);
}
