package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

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
