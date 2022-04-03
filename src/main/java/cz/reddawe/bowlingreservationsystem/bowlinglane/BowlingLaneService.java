package cz.reddawe.bowlingreservationsystem.bowlinglane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BowlingLaneService {

    private final BowlingLaneRepository bowlingLaneRepository;

    @Autowired
    public BowlingLaneService(BowlingLaneRepository bowlingLaneRepository) {
        this.bowlingLaneRepository = bowlingLaneRepository;
    }

    public BowlingLane addBowlingLane(BowlingLane bowlingLane) {
        int bowlingLaneNumber = bowlingLane.getNumber();

        if (bowlingLaneRepository.existsById(bowlingLaneNumber)) {
            throw new IllegalStateException(String.format("Lane %d already exists", bowlingLaneNumber));
        }
        return bowlingLaneRepository.save(bowlingLane);
    }

    public void removeBowlingLane(BowlingLane bowlingLane) {
        int bowlingLaneNumber = bowlingLane.getNumber();

        if (!bowlingLaneRepository.existsById(bowlingLaneNumber)) {
            throw new IllegalStateException(String.format("Lane %d is not in the system", bowlingLaneNumber));
        }
        bowlingLaneRepository.deleteById(bowlingLaneNumber);
    }

    public List<BowlingLane> getBowlingLanes() {
        return bowlingLaneRepository.findAll();
    }
}
