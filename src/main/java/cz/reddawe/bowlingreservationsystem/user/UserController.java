package cz.reddawe.bowlingreservationsystem.user;

import cz.reddawe.bowlingreservationsystem.user.iorecords.RoleName;
import cz.reddawe.bowlingreservationsystem.user.iorecords.UserInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for User entity.
 *
 * @author David Dvorak
 */
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Registers a new user",
            description = """
                    Username requires:                   
                     1. 3 to 255 characters
                     2. Only consists of upper and lowercase letters, digits and .@
                    
                    Password requires:
                     1. 8 to 50 characters
                     2. Only consists of upper and lowercase letters, digits and @$!%*#?&
                     3. At least one digit
                     4. At least one letter (can be either upper or lowercase)
                    """,
            responses = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "400")
            }
    )
    @PostMapping("register")
    public ResponseEntity<Void> registerUser(@RequestBody UserInput userInput) {
        userService.registerUser(userInput);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Returns role of the currently logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RoleName.class)
                    ))
            }
    )
    @GetMapping("role")
    public RoleName getMyRoleName() {
        return userService.getMyRoleName();
    }
}
