package cz.reddawe.bowlingreservationsystem.exceptions.forbidden;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}