package cz.reddawe.bowlingreservationsystem.exceptions;

public class ResourceDoesNotExistException extends ResourceException {

    public ResourceDoesNotExistException(String resource) {
        super("Resource does not exist", resource);
    }
}
