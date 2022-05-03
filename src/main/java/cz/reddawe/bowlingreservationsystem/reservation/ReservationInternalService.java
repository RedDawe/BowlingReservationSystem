package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithUsername;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Part of the service layer of the Reservation entity,
 * to be used by other objects of the service layer.
 *
 * @author David Dvorak
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class ReservationInternalService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationInternalService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationWithUsername> getReservationsByLane(BowlingLane bowlingLane) {
        return reservationRepository.findReservationsByBowlingLane(bowlingLane).stream()
                .map(ReservationUtils::reservationToReservationWithUsername)
                .toList();
    }

    @PreAuthorize("hasAuthority('BOWLING_LANE:DELETE')")
    public void forcefullyDeleteReservation(long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalStateException(String.format("Reservation %s does not exist", reservationId));
        }

        reservationRepository.deleteById(reservationId);
    }

    @PreAuthorize("hasAuthority('BOWLING_LANE:DELETE')")
    public void changeBowlingLane(long reservationId, BowlingLane bowlingLane) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new IllegalStateException(String.format("Reservation %s does not exist", reservationId))
        );

        reservation.setBowlingLane(bowlingLane);
    }
}
