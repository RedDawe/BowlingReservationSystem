package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("FROM Reservation r WHERE r.user = ?1")
    List<Reservation> findReservationsByUser(User user);
}
