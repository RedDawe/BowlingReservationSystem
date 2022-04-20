package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;

import java.time.LocalDateTime;

public record ReservationInput(
        LocalDateTime start,
        LocalDateTime end,
        int peopleComing,
        BowlingLane bowlingLane
) {
}
