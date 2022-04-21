package cz.reddawe.bowlingreservationsystem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("login")
    public void login(@RequestBody UserInput userInput) {
        userService.login(userInput);
    }
}
