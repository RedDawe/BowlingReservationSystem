package cz.reddawe.bowlingreservationsystem.reservation.iorecords;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;

import java.time.LocalDateTime;

public record ReservationWithoutUser(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        int peopleComing,
        BowlingLane bowlingLane
) {
}
