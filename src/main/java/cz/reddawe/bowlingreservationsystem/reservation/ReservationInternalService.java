package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class ReservationInternalService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationInternalService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getReservationsByLane(BowlingLane bowlingLane) {
        return reservationRepository.findReservationsByBowlingLane(bowlingLane);
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
