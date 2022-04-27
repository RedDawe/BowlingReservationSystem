package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

public class ResourceAlreadyExistsException extends BadRequestException {

    public ResourceAlreadyExistsException(String resource) {
        super("Resource already exists", resource);
    }
}
