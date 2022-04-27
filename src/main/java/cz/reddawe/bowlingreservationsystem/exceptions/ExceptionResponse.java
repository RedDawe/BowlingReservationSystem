package cz.reddawe.bowlingreservationsystem.exceptions;

import java.time.ZonedDateTime;

public record ExceptionResponse(
        String error,
        String reason,
        ZonedDateTime timestamp
) {
}
