package cz.reddawe.bowlingreservationsystem.exceptions;

public abstract class ResourceException extends RuntimeException {

    private final String reason;
    private final String resource;

    public ResourceException(String reason, String resource) {

        this.reason = reason;
        this.resource = resource;
    }

    public String getReason() {
        return reason;
    }

    public String getResource() {
        return resource;
    }
}
