package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLaneService;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ReservationDeletionTimeExpiredException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ReservationInvalidException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceDoesNotExistException;
import cz.reddawe.bowlingreservationsystem.exceptions.forbidden.ForbiddenException;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationInput;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithIsMineFlag;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithoutUser;
import cz.reddawe.bowlingreservationsystem.user.User;
import cz.reddawe.bowlingreservationsystem.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Part of the service layer of the Reservation entity,
 * to be directly used by the controller layer
 *
 * @author David Dvorak
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final BowlingLaneService bowlingLaneService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserService userService, BowlingLaneService bowlingLaneService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.bowlingLaneService = bowlingLaneService;
    }

    /**
     * Returns all reservations of all users.
     * Reservations of the currently logged-in user
     * have {@link ReservationWithIsMineFlag#isMine()}
     * set to true.
     *
     * @return all reservations
     */
    public List<ReservationWithIsMineFlag> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        User currentUser = userService.getCurrentUser().orElse(null);
        return reservations.stream()
                .map(reservation -> ReservationUtils.reservationToReservationWithIsMineFlag(reservation, currentUser))
                .toList();
    }

    /**
     * Returns reservations of the currently logged-in user.
     *
     * @return reservations of the currently logged-in user
     */
    @PreAuthorize("isAuthenticated()")
    public List<ReservationWithoutUser> getMyReservations() {
        User currentUser = userService.getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                    getMyReservations can only be called by an authenticated user"""));

        List<Reservation> reservationsByUser = reservationRepository.findReservationsByUser(currentUser);

        return reservationsByUser.stream()
                .map(ReservationUtils::reservationToReservationWithoutUser)
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
        if (!bowlingLaneService.doesBowlingLaneExist(reservationInput.bowlingLane().getNumber())) {
            throw new ReservationInvalidException("bowlingLane");
        }

        if (reservationInput.peopleComing() < 1) {
            throw new ReservationInvalidException("peopleComing");
        }

        if (reservationInput.start().compareTo(reservationInput.end()) >= 0) {
            throw new ReservationInvalidException("start>=end");
        }

        if (overlaps(reservationInput)) {
            throw new ReservationInvalidException("overlap");
        }
    }

    /**
     * Creates reservation.
     *
     * @param reservationInput to be created
     * @return the created reservation
     */
    @PreAuthorize("hasAuthority('RESERVATION:CREATE')")
    public ReservationWithoutUser createReservation(ReservationInput reservationInput) {
        throwIfNotValidReservation(reservationInput);
        User currentUser = userService.getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                createReservation can only be called by an authenticated user"""));
        Reservation reservation = ReservationUtils.reservationInputToReservation(reservationInput, currentUser);

        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationUtils.reservationToReservationWithoutUser(savedReservation);
    }

    private void throwIfInvalidDeleteRequest(Reservation reservation) {
        User currentUser = userService.getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                deleteReservation can only be called by an authenticated user"""));

        if (!currentUser.equals(reservation.getUser())) {
            throw new ForbiddenException("You attempted to delete someone else's reservation");
        }


        Duration timeUntilReservation = Duration.between(LocalDateTime.now(), reservation.getStart());

        if (timeUntilReservation.compareTo(Duration.ofHours(24)) < 0) {
            throw new ReservationDeletionTimeExpiredException(reservation.getStart().toString());
        }
    }

    /**
     * Deletes reservation.
     *
     * @param reservationId to be deleted
     */
    @PreAuthorize("hasAuthority('RESERVATION:DELETE')")
    public void deleteReservation(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceDoesNotExistException(String.valueOf(reservationId))
        );

        throwIfInvalidDeleteRequest(reservation);

        reservationRepository.deleteById(reservationId);
    }
}
