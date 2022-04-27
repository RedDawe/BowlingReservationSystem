package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

public class PasswordValidationException extends BadRequestException {

    public PasswordValidationException() {
        super("Password is invalid", "");
    }
}
