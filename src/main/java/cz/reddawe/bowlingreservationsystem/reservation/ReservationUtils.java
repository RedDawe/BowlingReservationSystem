package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationInput;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithIsMineFlag;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithUsername;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithoutUser;
import cz.reddawe.bowlingreservationsystem.user.User;

import javax.annotation.Nullable;

/**
 * Utils class to convert between {@link Reservation} and its IO records
 *
 * @author David Dvorak
 */
class ReservationUtils {

    static ReservationWithIsMineFlag reservationToReservationWithIsMineFlag(
            Reservation reservation, @Nullable User currentUser) {


        return new ReservationWithIsMineFlag(
                reservation.getId(), reservation.getStart(), reservation.getEnd(),
                reservation.getPeopleComing(), reservation.getUser().equals(currentUser),
                reservation.getBowlingLane());
    }

    static ReservationWithoutUser reservationToReservationWithoutUser(Reservation reservation) {

        return new ReservationWithoutUser(
                reservation.getId(), reservation.getStart(), reservation.getEnd(),
                reservation.getPeopleComing(), reservation.getBowlingLane());
    }

    static Reservation reservationInputToReservation(ReservationInput reservationInput, User currentUser) {
        return new Reservation(
                reservationInput.start(), reservationInput.end(),
                reservationInput.peopleComing(), currentUser, reservationInput.bowlingLane()
        );
    }

    static ReservationWithUsername reservationToReservationWithUsername(Reservation reservation) {
        return new ReservationWithUsername(reservation.getId(), reservation.getStart(), reservation.getEnd(),
                reservation.getPeopleComing(), reservation.getUser().getUsername(), reservation.getBowlingLane());
    }
}
