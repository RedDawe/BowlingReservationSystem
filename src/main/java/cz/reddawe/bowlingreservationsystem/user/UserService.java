package cz.reddawe.bowlingreservationsystem.user;

import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.authorization.RoleRepository;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.PasswordValidationException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceAlreadyExistsException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.UsernameValidationException;
import cz.reddawe.bowlingreservationsystem.user.iorecords.RoleName;
import cz.reddawe.bowlingreservationsystem.user.iorecords.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implements service layer for User entity.
 *
 * @author David Dvorak
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class UserService implements UserDetailsService {

    /**
     * Matches:
     * 1. 3 to 255 characters
     * 2. Only consists of upper and lowercase letters, digits and .@
     */
    private static final String USERNAME_REGEX = "^[a-zA-Z\\d@.]{3,255}$";
    /**
     * Matches:
     * 1. 8 to 50 characters
     * 2. Only consists of upper and lowercase letters, digits and @$!%*#?&
     * 3. At least one digit
     * 4. At least one letter (can be either upper or lowercase)
     */
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Z\\d@$!%*#?&]{8,50}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Username %s not found", username))
        );
    }

    /**
     * Returns the current user.
     *
     * @return an {@link Optional} of the currently logged-in user
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return Optional.empty();

        Object principal = authentication.getPrincipal();
        if (principal == null) throw new RuntimeException("Authentication returned null from getPrincipal");
        if (principal.equals("anonymousUser")) return Optional.empty();
        if (principal.getClass() != User.class) throw new RuntimeException("""
                Authentication::getPrincipal() returned object other than "anonymousUser" or
                cz.reddawe.bowlingreservationsystem.user.User
                """
        );
        return Optional.of((User) principal);
    }

    private static boolean validateUsername(String username) {
        return username.matches(USERNAME_REGEX);
    }

    private static boolean validatePassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    /**
     * Saves a new user to the database.
     *
     * @param userInput user to be registered
     */
    public void registerUser(UserInput userInput) {
        if (!validateUsername(userInput.username())) {
            throw new UsernameValidationException(userInput.username());
        }
        if (!validatePassword(userInput.password())) {
            throw new PasswordValidationException();
        }
        if (userRepository.findByUsername(userInput.username()).isPresent()) {
            throw new ResourceAlreadyExistsException(userInput.username());
        }

        String passwordHash = passwordEncoder.encode(userInput.password());
        Role userRole = roleRepository.getByName("USER");
        User user = new User(userInput.username(), passwordHash, userRole);

        userRepository.save(user);
    }

    /**
     * Returns role of the current user.
     *
     * @return string representation of the role of the currently logged-in user wrapped in a record
     */
    public RoleName getMyRoleName() {
        Optional<User> optionalUser = getCurrentUser();

        if (optionalUser.isEmpty()) {
            return new RoleName("anonymousUser");
        }
        return new RoleName(optionalUser.get().getRole().getName());
    }
}
