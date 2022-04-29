package cz.reddawe.bowlingreservationsystem.bowlinglane;

import cz.reddawe.bowlingreservationsystem.authorization.Authority;
import cz.reddawe.bowlingreservationsystem.authorization.AuthorityRepository;
import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.authorization.RoleRepository;
import cz.reddawe.bowlingreservationsystem.reservation.Reservation;
import cz.reddawe.bowlingreservationsystem.reservation.ReservationRepository;
import cz.reddawe.bowlingreservationsystem.user.User;
import cz.reddawe.bowlingreservationsystem.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class BowlingLaneRepositoryTest {

    @Autowired
    private BowlingLaneRepository underTest;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
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

        userRepository.save(new User("username", "password1", user));
    }

    @Test
    void itShouldfindAllInOrderByNumber() {
        // given
        BowlingLane bowlingLane2 = new BowlingLane(2);
        BowlingLane bowlingLane1 = new BowlingLane(1);

        bowlingLane2 = underTest.save(bowlingLane2);
        bowlingLane1 = underTest.save(bowlingLane1);

        // when
        List<BowlingLane> result = underTest.findAllByOrderByNumber();

        // then
        assertThat(result).asList().containsExactly(bowlingLane1, bowlingLane2);
    }

    @Test
    void itShouldFindAlternativeBowlingLaneFor() {
        // given
        BowlingLane current = new BowlingLane(1);
        BowlingLane alternative = new BowlingLane(2);

        current = underTest.save(current);
        alternative = underTest.save(alternative);

        reservationRepository.save(new Reservation(LocalDateTime.MAX.minusHours(1), LocalDateTime.MAX, 1,
                userRepository.getById(1L), alternative));

        // when
        List<BowlingLane> result = underTest.findAlternativeBowlingLaneFor(
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), current);

        // then
        assertThat(result).asList().containsExactly(alternative);
    }

    @Test
    void itShouldNotFindAlternativeBowlingLaneFor() {
        // given
        BowlingLane current = new BowlingLane(1);
        BowlingLane alternative = new BowlingLane(2);

        current = underTest.save(current);
        alternative = underTest.save(alternative);

        reservationRepository.save(new Reservation(LocalDateTime.now(), LocalDateTime.now().plusHours(1), 1,
                userRepository.findByUsername("username").get(), alternative));

        // when
        List<BowlingLane> result = underTest.findAlternativeBowlingLaneFor(
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), current);

        // then
        assertThat(result).asList().isEmpty();
    }
}