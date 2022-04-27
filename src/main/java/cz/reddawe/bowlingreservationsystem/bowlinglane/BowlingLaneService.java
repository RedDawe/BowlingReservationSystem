package cz.reddawe.bowlingreservationsystem.bowlinglane;

import cz.reddawe.bowlingreservationsystem.reservation.Reservation;
import cz.reddawe.bowlingreservationsystem.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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
    public BowlingLane createBowlingLane(BowlingLane bowlingLane) {
        int bowlingLaneNumber = bowlingLane.getNumber();

        if (bowlingLaneRepository.existsById(bowlingLaneNumber)) {
            throw new IllegalStateException(String.format("Lane %d already exists", bowlingLaneNumber));
        }
        return bowlingLaneRepository.save(bowlingLane);
    }

    private List<String> moveReservationsFromBowlingLane(BowlingLane toBeRemoved) {
        List<String> couldNotReassign = new LinkedList<>();
        List<Reservation> reservationsOnToBeRemovedBowlingLane = reservationService.getReservationsByLane(toBeRemoved);

        for (Reservation reservation : reservationsOnToBeRemovedBowlingLane) {

            List<BowlingLane> emptyLanesDuringReservation = bowlingLaneRepository.findAlternativeBowlingLaneFor(
                    reservation.getStart(), reservation.getEnd(), toBeRemoved
            );

            if (emptyLanesDuringReservation.size() == 0) {
                couldNotReassign.add(reservation.toString());
                reservationService.forcefullyDeleteReservation(reservation.getId());
                continue;
            }
            reservationService.changeBowlingLane(reservation.getId(), emptyLanesDuringReservation.get(0));
        }

        return couldNotReassign;
    }

    @PreAuthorize("hasAuthority('BOWLING_LANE:DELETE')")
    public List<String> deleteBowlingLane(int bowlingLaneNumber) {
        BowlingLane toBeRemoved = bowlingLaneRepository.findById(bowlingLaneNumber).orElseThrow(
                () -> new IllegalStateException(String.format("Lane %d does not exist", bowlingLaneNumber))
        );

        List<String> couldNotReassign = moveReservationsFromBowlingLane(toBeRemoved);

        bowlingLaneRepository.deleteById(bowlingLaneNumber);
        return couldNotReassign;
    }

    public List<BowlingLane> getBowlingLanesOrdered() {
        return bowlingLaneRepository.findAllByOrderByNumber();
    }
}
