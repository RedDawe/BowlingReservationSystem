package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

public class ReservationInvalidException extends BadRequestException {

    public ReservationInvalidException(String reason) {
        super("Reservation is invalid", reason);
    }
}
