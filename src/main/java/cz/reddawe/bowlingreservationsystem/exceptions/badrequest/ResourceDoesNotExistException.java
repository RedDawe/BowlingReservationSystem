package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

/**
 * Thrown when user attempted to delete a resource on the server that does not exist.
 *
 * @author David Dvorak
 */
public class ResourceDoesNotExistException extends BadRequestException {

    public ResourceDoesNotExistException(String resource) {
        super("Resource does not exist", resource);
    }
}
