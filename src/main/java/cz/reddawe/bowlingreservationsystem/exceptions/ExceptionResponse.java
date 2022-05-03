package cz.reddawe.bowlingreservationsystem.exceptions;

import java.time.ZonedDateTime;

/**
 * Record to be returned to the user when an exception occurs.
 *
 * @param error description of the error that occurred
 * @param reason reason why the error occurred
 * @param timestamp when the exception got caught
 *
 * @author David Dvorak
 */
public record ExceptionResponse(
        String error,
        String reason,
        ZonedDateTime timestamp
) {
}
