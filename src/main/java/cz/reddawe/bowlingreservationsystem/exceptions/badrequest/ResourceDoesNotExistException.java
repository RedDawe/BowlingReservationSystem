package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

public class ResourceDoesNotExistException extends BadRequestException {

    public ResourceDoesNotExistException(String resource) {
        super("Resource does not exist", resource);
    }
}
