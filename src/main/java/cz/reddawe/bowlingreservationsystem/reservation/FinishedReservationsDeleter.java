package cz.reddawe.bowlingreservationsystem.reservation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class FinishedReservationsDeleter {

    private final ReservationRepository reservationRepository;

    public FinishedReservationsDeleter(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Scheduled(cron = "@midnight")
    public void deleteFinishedReservations() {
        reservationRepository.deleteAllById(reservationRepository.findIdByExpired(LocalDateTime.now()));
    }
}
