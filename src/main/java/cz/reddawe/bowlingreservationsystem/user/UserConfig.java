package cz.reddawe.bowlingreservationsystem.user;

import cz.reddawe.bowlingreservationsystem.authorization.Authority;
import cz.reddawe.bowlingreservationsystem.authorization.AuthorityRepository;
import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.authorization.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class UserConfig {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserConfig(AuthorityRepository authorityRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void configureAuthorities(List<Authority> userAuthorities, List<Authority> managerAuthorities) {
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

    public void configureRoles(List<Authority> userAuthorities, List<Authority> managerAuthorities) {
        Role user = new Role("USER", userAuthorities);
        Role manager = new Role("MANAGER", managerAuthorities);

        roleRepository.saveAll(List.of(
                user,
                manager
        ));
    }

    public void configureManager() {
        Role managerRole = roleRepository.findByName("MANAGER");
        String rawPassword = "poet-homecoming-group-try";

        userRepository.save(
                new User("manager", passwordEncoder.encode(rawPassword),  managerRole)
        );
    }

    @Bean
    public void doConfiguration() {
        List<Authority> userAuthorities = new ArrayList<>();
        List<Authority> managerAuthorities = new ArrayList<>();

        configureAuthorities(userAuthorities, managerAuthorities);
        configureRoles(userAuthorities, managerAuthorities);
        configureManager();
    }
}
