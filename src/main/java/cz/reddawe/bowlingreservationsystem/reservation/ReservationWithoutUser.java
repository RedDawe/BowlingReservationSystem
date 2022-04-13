package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationWithoutUser(
        Long id,
        LocalTime start,
        LocalTime end,
        LocalDate date,
        int peopleComing,
        BowlingLane bowlingLane
) {
}
