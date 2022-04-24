package cz.reddawe.bowlingreservationsystem.user;

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
        Authority reservationManage = new Authority("RESERVATION:MANAGE");
        Authority bowlingLaneManage = new Authority("BOWLING_LANE:MANAGE");

        userAuthorities.add(
                reservationManage
        );
        managerAuthorities.addAll(List.of(
                reservationManage,
                bowlingLaneManage
        ));

        authorityRepository.saveAll(List.of(
                reservationManage, bowlingLaneManage
        ));
    }

    public Role configureRoles(List<Authority> userAuthorities, List<Authority> managerAuthorities) {
        Role user = new Role("USER", userAuthorities);
        Role manager = new Role("MANAGER", managerAuthorities);

        roleRepository.saveAll(List.of(
                user,
                manager
        ));

        return manager;
    }

    public void configureManager(Role managerRole) {
        userRepository.save(
                new User("manager", passwordEncoder.encode("poet-homecoming-group-try"), managerRole)
        );
    }

    @Bean
    public void doConfiguration() {
        List<Authority> userAuthorities = new ArrayList<>();
        List<Authority> managerAuthorities = new ArrayList<>();

        configureAuthorities(userAuthorities, managerAuthorities);
        Role manager = configureRoles(userAuthorities, managerAuthorities);
        configureManager(manager);
    }
}
