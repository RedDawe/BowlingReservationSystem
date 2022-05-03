package cz.reddawe.bowlingreservationsystem.bowlinglane;

import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceAlreadyExistsException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceDoesNotExistException;
import cz.reddawe.bowlingreservationsystem.reservation.Reservation;
import cz.reddawe.bowlingreservationsystem.reservation.ReservationInternalService;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithUsername;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements service layer for BowlingLane entity.
 *
 * @author David Dvorak
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class BowlingLaneService {

    private final ReservationInternalService reservationInternalService;
    private final BowlingLaneRepository bowlingLaneRepository;

    @Autowired
    public BowlingLaneService(ReservationInternalService reservationInternalService,
                              BowlingLaneRepository bowlingLaneRepository) {
        this.reservationInternalService = reservationInternalService;
        this.bowlingLaneRepository = bowlingLaneRepository;
    }

    /**
     * Creates bowlingLane.
     *
     * @param bowlingLane to be created
     * @return created bowlingLane
     */
    @PreAuthorize("hasAuthority('BOWLING_LANE:CREATE')")
    public BowlingLane createBowlingLane(BowlingLane bowlingLane) {
        int bowlingLaneNumber = bowlingLane.getNumber();

        if (bowlingLaneRepository.existsById(bowlingLaneNumber)) {
            throw new ResourceAlreadyExistsException(String.valueOf(bowlingLaneNumber));
        }
        return bowlingLaneRepository.save(bowlingLane);
    }

    private List<String> moveReservationsFromBowlingLane(BowlingLane toBeRemoved) {
        List<String> couldNotReassign = new LinkedList<>();
        List<ReservationWithUsername> reservationsOnToBeRemovedBowlingLane = reservationInternalService
                .getReservationsByLane(toBeRemoved);

        for (ReservationWithUsername reservation : reservationsOnToBeRemovedBowlingLane) {

            List<BowlingLane> emptyLanesDuringReservation = bowlingLaneRepository.findAlternativeBowlingLaneFor(
                    reservation.start(), reservation.end(), toBeRemoved);

            if (emptyLanesDuringReservation.size() == 0) {
                couldNotReassign.add(reservation.toString());
                reservationInternalService.forcefullyDeleteReservation(reservation.id());
                continue;
            }
            reservationInternalService.changeBowlingLane(reservation.id(), emptyLanesDuringReservation.get(0));
        }

        return couldNotReassign;
    }

    /**
     * Deletes bowlingLane.
     *
     * Before deleting the bowlingLane attempts to
     * find alternative bowlingLanes for reservations
     * that are currently on the lane that is being deleted.
     *
     * If alternative bowlingLane can't be found for
     * a reservation, the reservation is deleted and
     * its string representation returned instead.
     *
     * @param bowlingLaneNumber to be deleted
     * @return string representation of reservation that had to be deleted
     */
    @PreAuthorize("hasAuthority('BOWLING_LANE:DELETE')")
    public List<String> deleteBowlingLane(int bowlingLaneNumber) {
        BowlingLane toBeRemoved = bowlingLaneRepository.findById(bowlingLaneNumber).orElseThrow(
                () -> new ResourceDoesNotExistException(String.valueOf(bowlingLaneNumber))
        );

        List<String> couldNotReassign = moveReservationsFromBowlingLane(toBeRemoved);

        bowlingLaneRepository.deleteById(bowlingLaneNumber);
        return couldNotReassign;
    }

    /**
     * Returns bowlingLane ordered ascending by {@link BowlingLane#getNumber()}.
     *
     * @return ordered bowlingLanes
     */
    public List<BowlingLane> getBowlingLanesOrdered() {
        return bowlingLaneRepository.findAllByOrderByNumber();
    }

    /**
     * Returns whether bowlingLane with bowlingLaneNumber exists.
     *
     * @param bowlingLaneNumber to be checked
     * @return true if bowlingLane exists in the database
     */
    public boolean doesBowlingLaneExist(int bowlingLaneNumber) {
        return bowlingLaneRepository.existsById(bowlingLaneNumber);
    }
}
