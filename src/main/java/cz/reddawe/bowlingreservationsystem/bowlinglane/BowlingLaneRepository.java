package cz.reddawe.bowlingreservationsystem.bowlinglane;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BowlingLaneRepository extends JpaRepository<BowlingLane, Integer> {

    @Query("FROM BowlingLane b ORDER BY b.number")
    List<BowlingLane> findAllByOrderByNumber();

    @Query("""
        FROM BowlingLane b WHERE b <> ?3 AND b NOT IN (
            SELECT r.bowlingLane FROM Reservation r WHERE r.start < ?2 AND  r.end > ?1
        )
    """)
    List<BowlingLane> findAlternativeBowlingLaneFor(LocalDateTime start, LocalDateTime end, BowlingLane current);
}
