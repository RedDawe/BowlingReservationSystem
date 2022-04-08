package cz.reddawe.bowlingreservationsystem.user;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public void registerUser(User user) {
        if (!validateUsername(user.getUsername())) {
            throw new IllegalArgumentException(String.format("Username %s is not valid", user.getUsername()));
        }
        if (!validatePassword(user.getPassword())) {
            throw new IllegalArgumentException("Password is not valid");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException(String.format("Username %s already exists", user.getUsername()));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }
}
