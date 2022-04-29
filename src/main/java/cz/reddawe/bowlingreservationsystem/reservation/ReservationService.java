package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ReservationDeletionTimeExpiredException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ReservationValidationException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceDoesNotExistException;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationInput;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithIsMineFlag;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithoutUser;
import cz.reddawe.bowlingreservationsystem.user.User;
import cz.reddawe.bowlingreservationsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserService userService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
    }

    private static ReservationWithIsMineFlag reservationToReservationWithIsMineFlag(
            Reservation reservation, Optional<User> currentUser) {


        return new ReservationWithIsMineFlag(
                reservation.getId(), reservation.getStart(), reservation.getEnd(),
                reservation.getPeopleComing(), reservation.getUser().equals(currentUser.orElse(null)),
                reservation.getBowlingLane());
    }

    private static ReservationWithoutUser reservationToReservationWithoutUser(Reservation reservation) {

        return new ReservationWithoutUser(
                reservation.getId(), reservation.getStart(), reservation.getEnd(),
                reservation.getPeopleComing(), reservation.getBowlingLane());
    }

    private static Reservation reservationInputToReservation(ReservationInput reservationInput, User currentUser) {
        return new Reservation(
                reservationInput.start(), reservationInput.end(),
                reservationInput.peopleComing(), currentUser, reservationInput.bowlingLane()
        );
    }

    public List<ReservationWithIsMineFlag> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        Optional<User> currentUser = userService.getCurrentUser();
        return reservations.stream()
                .map(reservation -> reservationToReservationWithIsMineFlag(reservation, currentUser))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public List<ReservationWithoutUser> getMyReservations() {
        List<Reservation> reservationsByUser = reservationRepository.findReservationsByUser(
                userService.getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                            getMyReservations can only be called by an authenticated user
                        """))
        );

        return reservationsByUser.stream()
                .map(ReservationService::reservationToReservationWithoutUser)
                .toList();
    }

    private boolean overlaps(ReservationInput reservationInput) {
        return reservationRepository
                .findReservationsByOverlap(reservationInput.start(),
                        reservationInput.end(), reservationInput.bowlingLane()
                )
                .size() > 0;
    }

    private void throwIfNotValidReservation(ReservationInput reservationInput) {
        if (reservationInput.peopleComing() < 1) {
            throw new ReservationValidationException("peopleComing");
        }

        if (reservationInput.start().compareTo(reservationInput.end()) >= 0) {
            throw new ReservationValidationException("start>=end");
        }

        if (overlaps(reservationInput)) {
            throw new ReservationValidationException("overlap");
        }
    }

    @PreAuthorize("hasAuthority('RESERVATION:CREATE')")
    public ReservationWithoutUser createReservation(ReservationInput reservationInput) {
        throwIfNotValidReservation(reservationInput);
        User currentUser = userService.getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                createReservation can only be called by an authenticated user"""));
        Reservation reservation = reservationInputToReservation(reservationInput, currentUser);

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationToReservationWithoutUser(savedReservation);
    }

    @PreAuthorize("hasAuthority('RESERVATION:DELETE')")
    public void deleteReservation(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceDoesNotExistException(String.valueOf(reservationId))
        );
        Duration timeUntilReservation = Duration.between(LocalDateTime.now(), reservation.getStart());

        if (timeUntilReservation.compareTo(Duration.ofHours(24)) < 0) {
            throw new ReservationDeletionTimeExpiredException(reservation.getStart().toString());
        }

        reservationRepository.deleteById(reservationId);
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
    @Transactional
    public void changeBowlingLane(long reservationId, BowlingLane bowlingLane) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new IllegalStateException(String.format("Reservation %s does not exist", reservationId))
        );

        reservation.setBowlingLane(bowlingLane);
    }
}
