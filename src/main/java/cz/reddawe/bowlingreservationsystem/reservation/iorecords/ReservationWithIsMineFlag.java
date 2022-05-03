package cz.reddawe.bowlingreservationsystem.reservation.iorecords;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;

import java.time.LocalDateTime;

/**
 * Represents User entity without boolean instead of a User.
 *
 * The isMine boolean is used to indicate whether the Reservation
 * belongs to the user to which this object is being returned.
 *
 * @author David Dvorak
 */
public record ReservationWithIsMineFlag(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        int peopleComing,
        boolean isMine,
        BowlingLane bowlingLane
) {
}
