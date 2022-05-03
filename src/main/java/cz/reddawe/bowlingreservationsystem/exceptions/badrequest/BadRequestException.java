package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

/**
 * Thrown when the server encounters a bad request.
 *
 * @author David Dvorak
 */
public abstract class BadRequestException extends RuntimeException {

    private final String error;
    private final String reason;

    public BadRequestException(String error, String reason) {

        this.error = error;
        this.reason = reason;
    }

    public String getError() {
        return error;
    }

    public String getReason() {
        return reason;
    }
}
