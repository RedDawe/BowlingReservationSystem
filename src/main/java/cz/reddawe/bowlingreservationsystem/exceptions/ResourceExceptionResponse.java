package cz.reddawe.bowlingreservationsystem.exceptions;

import java.time.ZonedDateTime;

public record ResourceExceptionResponse(
        String reason,
        String resource,
        ZonedDateTime timestamp
) {
}
