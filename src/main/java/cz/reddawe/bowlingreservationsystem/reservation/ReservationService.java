package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation[] getMyReservations() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (! (principal instanceof User currentUser)) {
            throw new RuntimeException("SecurityContextHolder returned invalid object from getPrincipal");
        }

        return reservationRepository.findReservationsByUser(currentUser);
    }

    public Reservation[] getAllReservations() {
        return reservationRepository.findAll().toArray(Reservation[]::new);
    }

    public Reservation createReservation(Reservation reservation) {
        long reservationId = reservation.getId();

        if (reservationRepository.existsById(reservationId)) {
            throw new IllegalStateException(String.format("Reservation %s already exists", reservationId));
        }

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalStateException(String.format("Reservation %s does not exist", reservationId));
        }

        reservationRepository.deleteById(reservationId);
    }
}
