package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    private static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (! (principal instanceof User currentUser)) {
            throw new RuntimeException("SecurityContextHolder returned invalid object from getPrincipal");
        }

        return currentUser;
    }

    private static void nullUser(Reservation reservation) {
        reservation.setUser(null);
    }

    private static void nullUsers(List<Reservation> reservations) {
        reservations.forEach(ReservationService::nullUser);
    }

    public List<Reservation> getMyReservations() {
        User currentUser = getCurrentUser();

        List<Reservation> reservationsByUser = reservationRepository.findReservationsByUser(currentUser);
        nullUsers(reservationsByUser);

        return reservationsByUser;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        nullUsers(reservations);

        return reservations;
    }

    public Reservation createReservation(Reservation reservation) {
        reservation.setUser(getCurrentUser());

        Reservation savedReservation = reservationRepository.save(reservation);

        nullUser(savedReservation);
        return savedReservation;
    }

    public void deleteReservation(long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalStateException(String.format("Reservation %s does not exist", reservationId));
        }

        reservationRepository.deleteById(reservationId);
    }
}
