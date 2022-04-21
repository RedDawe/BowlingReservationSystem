package cz.reddawe.bowlingreservationsystem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private static final String usernameRegex = "^[a-zA-Z0-9]{3,255}$";
    private static final String passwordRegex = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z\\d@$!%*#?&]{8,50}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Username %s not found", username))
        );
    }

    /**
     * Requirements:
     * 1. 3 to 255 characters
     * 2. Only consists of upper and lowercase letters and digits
     *
     * @param username
     * @return whether username satisfies requirements
     */
    private static boolean validateUsername(String username) {
        return username.matches(usernameRegex);
    }

    /**
     * Requirements:
     * 1. 8 to 50 characters
     * 2. Only consists of upper and lowercase letters, digits and @$!%*#?&
     * 3. At least one digit
     * 4. At least one letter (can be either upper or lowercase)
     *
     * @param password
     * @return whether password satisfies requirements
     */
    private static boolean validatePassword(String password) {
        return password.matches(passwordRegex);
    }

    public void registerUser(UserInput userInput) {
        if (!validateUsername(userInput.username())) {
            throw new IllegalArgumentException(String.format("Username %s is not valid", userInput.username()));
        }
        if (!validatePassword(userInput.password())) {
            throw new IllegalArgumentException("Password is not valid");
        }
        if (userRepository.findByUsername(userInput.username()).isPresent()) {
            throw new IllegalStateException(String.format("Username %s already exists", userInput.username()));
        }

        User user = new User(userInput.username(), passwordEncoder.encode(userInput.password()));

        userRepository.save(user);
    }

    public void login(UserInput userInput) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userInput.username(), userInput.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
