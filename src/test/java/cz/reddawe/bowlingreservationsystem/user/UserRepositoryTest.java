package cz.reddawe.bowlingreservationsystem.user;

import cz.reddawe.bowlingreservationsystem.authorization.Authority;
import cz.reddawe.bowlingreservationsystem.authorization.AuthorityRepository;
import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.authorization.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @BeforeEach
    void setUp() {
        Authority reservationCreate = new Authority("RESERVATION:CREATE");
        Authority reservationDelete = new Authority("RESERVATION:DELETE");

        Authority bowlingLaneCreate = new Authority("BOWLING_LANE:CREATE");
        Authority bowlingLaneDelete = new Authority("BOWLING_LANE:DELETE");

        authorityRepository.saveAll(List.of(reservationCreate, reservationDelete, bowlingLaneCreate,
                bowlingLaneDelete));

        Role user = new Role("USER", List.of(reservationCreate, reservationDelete));
        Role manager = new Role("MANAGER", List.of(bowlingLaneCreate, bowlingLaneDelete));

        roleRepository.saveAll(List.of(user, manager));
    }

    @Test
    void itShouldFindUserByUsername() {
        // given
        User user = new User("username", "password1", roleRepository.getByName("USER"));
        user = underTest.save(user);

        // when
        Optional<User> result = underTest.findByUsername("username");

        // then
        assertThat(result).isPresent().hasValue(user);
    }

    @Test
    void itShouldNotFindUserByUsername() {
        // given
        User user = new User("username", "password1", roleRepository.getByName("USER"));
        underTest.save(user);

        // when
        Optional<User> result = underTest.findByUsername("username1");

        // then
        assertThat(result).isEmpty();
    }
}