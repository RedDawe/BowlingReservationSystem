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

    public BowlingLane addBowlingLane(int number) {
        if (bowlingLaneRepository.findById(number).isPresent()) {
            throw new IllegalStateException(String.format("Lane %d already exists", number));
        }

        return bowlingLaneRepository.save(new BowlingLane(number));
    }

    public void removeBowlingLane(int number) {
        if (bowlingLaneRepository.existsById(number)) {
            throw new IllegalStateException(String.format("Lane %d does is not in the system", number));
        }

        bowlingLaneRepository.deleteById(number);
    }

    public List<Integer> getBowlingLanes() {
        return bowlingLaneRepository.findAll().stream().map(BowlingLane::getNumber).toList();
    }
}
