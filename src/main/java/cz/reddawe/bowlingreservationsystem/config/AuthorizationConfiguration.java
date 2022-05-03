package cz.reddawe.bowlingreservationsystem.config;

import cz.reddawe.bowlingreservationsystem.authorization.Authority;
import cz.reddawe.bowlingreservationsystem.authorization.AuthorityRepository;
import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.authorization.RoleRepository;
import cz.reddawe.bowlingreservationsystem.user.User;
import cz.reddawe.bowlingreservationsystem.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds authorities, roles and manager account to database.
 *
 * Authorities:
 *  RESERVATION:CREATE
 *  RESERVATION:DELETE
 *  BOWLING_LANE:CREATE
 *  BOWLING_LANE:DELETE
 *
 * Roles:
 *  USER with authorities: RESERVATION:CREATE, RESERVATION:DELETE
 *  MANAGER with authorities: BOWLING_LANE:CREATE, BOWLING_LANE:DELETE
 *
 * Users:
 *  manager with role: MANAGER
 *
 * @author David Dvorak
 */
@Configuration
public class AuthorizationConfiguration {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PropertiesConfig propertiesConfig;

    public AuthorizationConfiguration(AuthorityRepository authorityRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, PropertiesConfig propertiesConfig) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.propertiesConfig = propertiesConfig;
    }

    private void configureAuthorities(List<Authority> userAuthorities, List<Authority> managerAuthorities) {
        Authority reservationCreate = new Authority("RESERVATION:CREATE");
        Authority reservationDelete = new Authority("RESERVATION:DELETE");

        Authority bowlingLaneCreate = new Authority("BOWLING_LANE:CREATE");
        Authority bowlingLaneDelete = new Authority("BOWLING_LANE:DELETE");

        userAuthorities.addAll(List.of(
                reservationCreate, reservationDelete
        ));
        managerAuthorities.addAll(List.of(
                bowlingLaneCreate, bowlingLaneDelete
        ));

        authorityRepository.saveAll(List.of(
                reservationCreate, reservationDelete,
                bowlingLaneCreate, bowlingLaneDelete
        ));
    }

    private void configureRoles(List<Authority> userAuthorities, List<Authority> managerAuthorities) {
        Role user = new Role("USER", userAuthorities);
        Role manager = new Role("MANAGER", managerAuthorities);

        roleRepository.saveAll(List.of(
                user,
                manager
        ));
    }

    private void configureManager() {
        Role managerRole = roleRepository.getByName("MANAGER");
        String rawPassword = propertiesConfig.getManagerPassword();

        userRepository.save(
                new User("manager", passwordEncoder.encode(rawPassword),  managerRole)
        );
    }

    @Bean
    public void doConfiguration() {
        if (userRepository.findByUsername("manager").isPresent()) return;

        List<Authority> userAuthorities = new ArrayList<>();
        List<Authority> managerAuthorities = new ArrayList<>();

        configureAuthorities(userAuthorities, managerAuthorities);
        configureRoles(userAuthorities, managerAuthorities);
        configureManager();
    }
}
