package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

public class ReservationDeletionTimeExpiredException extends BadRequestException {

    public ReservationDeletionTimeExpiredException(String startTime) {
        super("Reservations can only be deleted 24 hours before they are supposed to start", startTime);
    }
}
