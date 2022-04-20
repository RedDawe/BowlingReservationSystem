package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("FROM Reservation r WHERE r.user = ?1")
    List<Reservation> findReservationsByUser(User user);

    @Query("FROM Reservation r WHERE r.bowlingLane = ?1 AND r.start < ?2 AND r.end > ?3")
    List<Reservation> findReservationsByBowlingLaneAndStartBeforeAndEndAfter
            (BowlingLane bowlingLane, LocalDateTime end, LocalDateTime start);

    @Query("FROM Reservation r WHERE r.bowlingLane = ?1")
    List<Reservation> findReservationsByBowlingLane(BowlingLane bowlingLane);
}
