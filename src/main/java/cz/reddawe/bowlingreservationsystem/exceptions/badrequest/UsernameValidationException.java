package cz.reddawe.bowlingreservationsystem.exceptions.badrequest;

public class UsernameValidationException extends BadRequestException {

    public UsernameValidationException(String username) {
        super("Username is invalid", username);
    }
}
