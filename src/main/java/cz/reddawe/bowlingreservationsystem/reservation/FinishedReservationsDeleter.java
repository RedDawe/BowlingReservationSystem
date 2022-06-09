package cz.reddawe.bowlingreservationsystem.reservation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Deletes reservations that have already ended every midnight.
 *
 * @author David Dvorak
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class FinishedReservationsDeleter {

    private final ReservationRepository reservationRepository;

    public FinishedReservationsDeleter(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Deletes reservations that have already ended.
     */
    @Scheduled(cron = "@midnight")
    public void deleteFinishedReservations() {
        reservationRepository.deleteAllById(reservationRepository.findIdByExpired(LocalDateTime.now()));
    }
}
