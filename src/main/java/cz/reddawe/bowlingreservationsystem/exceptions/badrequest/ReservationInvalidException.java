package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

/**
 * Thrown when user attempted to create an invalid reservation.
 *
 * @author David Dvorak
 */
public class ReservationInvalidException extends BadRequestException {

    public ReservationInvalidException(String reason) {
        super("Reservation is invalid", reason);
    }
}
