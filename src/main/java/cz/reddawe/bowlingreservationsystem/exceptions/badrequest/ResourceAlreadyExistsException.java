package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

/**
 * Thrown when user attempted to create a unique resource on the server that already exists.
 *
 * @author David Dvorak
 */
public class ResourceAlreadyExistsException extends BadRequestException {

    public ResourceAlreadyExistsException(String resource) {
        super("Resource already exists", resource);
    }
}
