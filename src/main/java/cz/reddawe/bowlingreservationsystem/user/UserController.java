package cz.reddawe.bowlingreservationsystem.user;

import cz.reddawe.bowlingreservationsystem.user.iorecords.RoleName;
import cz.reddawe.bowlingreservationsystem.user.iorecords.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<Void> registerUser(@RequestBody UserInput userInput) {
        userService.registerUser(userInput);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("role")
    public RoleName getMyRoleName() {
        return userService.getMyRoleName();
    }
}
