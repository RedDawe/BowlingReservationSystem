package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

public class ReservationValidationException extends BadRequestException {

    public ReservationValidationException(String reason) {
        super("Reservation is invalid", reason);
    }
}
