package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

/**
 * Thrown when user attempted to delete a reservation less than 24 hours before it starts.
 *
 * @author David Dvorak
 */
public class ReservationDeletionTimeExpiredException extends BadRequestException {

    public ReservationDeletionTimeExpiredException(String startTime) {
        super("Reservations can only be deleted 24 hours before they are supposed to start", startTime);
    }
}
