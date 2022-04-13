package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    private static Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) throw new RuntimeException("Authentication returned null from getPrincipal");
        if (principal.equals("anonymousUser")) return Optional.empty();
        if (principal.getClass() != User.class) throw new RuntimeException("""
                Authentication::getPrincipal() returned object other than "anonymousUser" or
                cz.reddawe.bowlingreservationsystem.user.User
                """
        );
        return Optional.of((User) principal);
    }

    private static ReservationWithIsMineFlag reservationToReservationWithIsMineFlag(
            Reservation reservation, Optional<User> currentUser) {


        return new ReservationWithIsMineFlag(
                reservation.getId(), reservation.getStart(), reservation.getEnd(), reservation.getDate(),
                reservation.getPeopleComing(), reservation.getUser().equals(currentUser.orElse(null)),
                reservation.getBowlingLane());
    }

    private static ReservationWithoutUser reservationToReservationWithoutUser(Reservation reservation) {

        return new ReservationWithoutUser(
                reservation.getId(), reservation.getStart(), reservation.getEnd(), reservation.getDate(),
                reservation.getPeopleComing(), reservation.getBowlingLane());
    }

    private static Reservation reservationInputToReservation(ReservationInput reservationInput, User currentUser) {
        return new Reservation(
                reservationInput.start(), reservationInput.end(), reservationInput.date(),
                reservationInput.peopleComing(), currentUser, reservationInput.bowlingLane()
        );
    }

    public List<ReservationWithIsMineFlag> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        Optional<User> currentUser = getCurrentUser();
        return reservations.stream()
                .map(reservation -> reservationToReservationWithIsMineFlag(reservation, currentUser))
                .toList();
    }

    public List<ReservationWithoutUser> getMyReservations() {
        List<Reservation> reservationsByUser = reservationRepository.findReservationsByUser(
                getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                            getMyReservations can only be called by an authorized user
                        """))
        );

        return reservationsByUser.stream()
                .map(ReservationService::reservationToReservationWithoutUser)
                .toList();
    }

    public ReservationWithoutUser createReservation(ReservationInput reservationInput) {
        Reservation reservation = reservationInputToReservation(reservationInput,
                getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                            createReservation can only be called by an authorized user
                        """))
        );

        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationToReservationWithoutUser(savedReservation);
    }

    public void deleteReservation(long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalStateException(String.format("Reservation %s does not exist", reservationId));
        }

        reservationRepository.deleteById(reservationId);
    }
}
