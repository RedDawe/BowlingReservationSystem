package cz.reddawe.bowlingreservationsystem.bowlinglane;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BowlingLaneRepository extends JpaRepository<BowlingLane, Integer> {
}
