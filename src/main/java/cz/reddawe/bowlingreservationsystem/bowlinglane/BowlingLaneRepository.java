package cz.reddawe.bowlingreservationsystem.bowlinglane;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface BowlingLaneRepository extends JpaRepository<BowlingLane, Integer> {

    @Query("FROM BowlingLane b ORDER BY b.number")
    List<BowlingLane> findAllByOrderByNumber();

    @Query("FROM BowlingLane b WHERE b.number <> ?1")
    List<BowlingLane> findBowlingLanesByNumberNot(Integer number);
}
