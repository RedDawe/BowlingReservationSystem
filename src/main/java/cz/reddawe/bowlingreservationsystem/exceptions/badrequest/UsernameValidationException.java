package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

/**
 * Thrown when user attempted to register with an invalid username.
 *
 * @author David Dvorak
 */
public class UsernameValidationException extends BadRequestException {

    public UsernameValidationException(String username) {
        super("Username is invalid", username);
    }
}
