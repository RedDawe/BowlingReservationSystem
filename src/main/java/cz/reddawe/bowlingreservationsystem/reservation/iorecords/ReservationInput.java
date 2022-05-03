package cz.reddawe.bowlingreservationsystem.reservation.iorecords;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;

import java.time.LocalDateTime;

/**
 * Represents Reservation entity without id and user.
 *
 * @author David Dvorak
 */
public record ReservationInput(
        LocalDateTime start,
        LocalDateTime end,
        int peopleComing,
        BowlingLane bowlingLane
) {
}
