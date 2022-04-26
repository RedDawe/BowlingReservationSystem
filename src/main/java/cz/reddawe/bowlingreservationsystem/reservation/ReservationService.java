package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

        Optional<User> currentUser = getCurrentUser();
        return reservations.stream()
                .map(reservation -> reservationToReservationWithIsMineFlag(reservation, currentUser))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
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

    private boolean overlaps(BowlingLane bowlingLane, LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findReservationsByBowlingLaneAndStartBeforeAndEndAfter(bowlingLane, end, start)
                .size() > 0;
    }

    private boolean overlaps(ReservationInput reservationInput) {
        return overlaps(reservationInput.bowlingLane(),
                reservationInput.start(), reservationInput.end());
    }

    private void throwIfNotValidReservation(ReservationInput reservationInput) {
        if (reservationInput.peopleComing() < 1) {
            throw new IllegalArgumentException("Reservation.peopleComing has to be at least 1");
        }

        if (overlaps(reservationInput)) {
            throw new IllegalStateException(
                    "Reservation cannot be created because it would overlap with another reservation"
            );
        }
    }

    @PreAuthorize("hasAuthority('RESERVATION:CREATE')")
    public ReservationWithoutUser createReservation(ReservationInput reservationInput) {
        throwIfNotValidReservation(reservationInput);
        User currentUser = getCurrentUser().orElseThrow(() -> new IllegalStateException("""
                createReservation can only be called by an authorized user"""));
        Reservation reservation = reservationInputToReservation(reservationInput, currentUser);

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationToReservationWithoutUser(savedReservation);
    }

    @PreAuthorize("hasAuthority('RESERVATION:DELETE')")
    public void deleteReservation(long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalStateException(String.format("Reservation %s does not exist", reservationId));
        }

        reservationRepository.deleteById(reservationId);
    }

    @Transactional
    protected boolean reassignReservation(Reservation reservation, List<BowlingLane> allOtherLanes) {
        for (BowlingLane bowlingLane : allOtherLanes) {
            if (overlaps(bowlingLane, reservation.getStart(), reservation.getEnd())) {
                continue;
            }

            reservation.setBowlingLane(bowlingLane);
            return true;
        }

        return false;
    }

    @PreAuthorize("hasAuthority('BOWLING_LANE:DELETE')")
    public List<String> reassignReservationsFromLane(BowlingLane reassignFrom, List<BowlingLane> allOtherLanes) {
        List<Reservation> toBeReassigned = reservationRepository.findReservationsByBowlingLane(reassignFrom);
        List<String> couldNotReassign = new ArrayList<>();

        for (Reservation reservation : toBeReassigned) {
            if (!reassignReservation(reservation, allOtherLanes)) {
                couldNotReassign.add(reservation.toString());
                reservationRepository.delete(reservation);
            }
        }

        return couldNotReassign;
    }
}
