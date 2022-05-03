package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

/**
 * Thrown when user attempted to register with an invalid password.
 *
 * @author David Dvorak
 */
public class PasswordValidationException extends BadRequestException {

    public PasswordValidationException() {
        super("Password is invalid", "");
    }
}
