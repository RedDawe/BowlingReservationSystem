package cz.reddawe.bowlingreservationsystem.exceptions;

public class ResourceAlreadyExistsException extends ResourceException {

    public ResourceAlreadyExistsException(String resource) {
        super("Resource already exists", resource);
    }
}
