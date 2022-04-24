package cz.reddawe.bowlingreservationsystem.bowlinglane;

import cz.reddawe.bowlingreservationsystem.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BowlingLaneService {

    private final ReservationService reservationService;
    private final BowlingLaneRepository bowlingLaneRepository;

    @Autowired
    public BowlingLaneService(ReservationService reservationService, BowlingLaneRepository bowlingLaneRepository) {
        this.reservationService = reservationService;
        this.bowlingLaneRepository = bowlingLaneRepository;
    }

    @PreAuthorize("hasAuthority('BOWLING_LANE:CREATE')")
    public BowlingLane addBowlingLane(BowlingLane bowlingLane) {
        int bowlingLaneNumber = bowlingLane.getNumber();

        if (bowlingLaneRepository.existsById(bowlingLaneNumber)) {
            throw new IllegalStateException(String.format("Lane %d already exists", bowlingLaneNumber));
        }
        return bowlingLaneRepository.save(bowlingLane);
    }

    @PreAuthorize("hasAuthority('BOWLING_LANE:DELETE')")
    public List<String> removeBowlingLane(int bowlingLaneNumber) {
        BowlingLane toBeRemoved = bowlingLaneRepository.findById(bowlingLaneNumber).orElseThrow(
                () -> new IllegalStateException(String.format("Lane %d does not exist", bowlingLaneNumber)));
        List<BowlingLane> allOtherBowlingLanes = bowlingLaneRepository.findBowlingLanesByNumberNot(bowlingLaneNumber);

        List<String> couldNotReassign = reservationService.reassignReservationsFromLane(
                toBeRemoved, allOtherBowlingLanes);

        bowlingLaneRepository.deleteById(bowlingLaneNumber);
        return couldNotReassign;
    }

    public List<BowlingLane> getBowlingLanesOrdered() {
        return bowlingLaneRepository.findAllByOrderByNumber();
    }
}
