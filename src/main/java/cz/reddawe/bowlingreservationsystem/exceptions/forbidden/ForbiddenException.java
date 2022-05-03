package cz.reddawe.bowlingreservationsystem.exceptions.forbidden;

/**
 * Thrown when an action that is forbidden was attempted.
 *
 * @author David Dvorak
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
