package cz.reddawe.bowlingreservationsystem.reservation.iorecords;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;

import java.time.LocalDateTime;

/**
 * Represents Reservation entity with String username instead of a User object.
 *
 * @author David Dvorak
 */
public record ReservationWithUsername(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        int peopleComing,
        String username,
        BowlingLane bowlingLane
) {
}
