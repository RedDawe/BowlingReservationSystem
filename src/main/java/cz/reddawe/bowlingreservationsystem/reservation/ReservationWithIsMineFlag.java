package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;

import java.time.LocalDateTime;

public record ReservationWithIsMineFlag(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        int peopleComing,
        boolean isMine,
        BowlingLane bowlingLane
) {
}
