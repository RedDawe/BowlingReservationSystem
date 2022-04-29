package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.authorization.Authority;
import cz.reddawe.bowlingreservationsystem.authorization.AuthorityRepository;
import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.authorization.RoleRepository;
import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLaneRepository;
import cz.reddawe.bowlingreservationsystem.user.User;
import cz.reddawe.bowlingreservationsystem.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository underTest;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BowlingLaneRepository bowlingLaneRepository;
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
    void itShouldFindReservationByUser() {
        // given
        User user = new User("username", "password1", roleRepository.findByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByUser(user);

        // then
        assertThat(reservationsByUser).asList().containsExactly(reservation);
    }

    @Test
    void itShouldNotFindReservationByUser() {
        // given
        User user1 = new User("username1", "password1", roleRepository.findByName("USER"));
        user1 = userRepository.save(user1);
        User user2 = new User("username2", "password1", roleRepository.findByName("USER"));
        user2 = userRepository.save(user2);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation = new Reservation(start, end, 1, user1, bowlingLane);
        underTest.save(reservation);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByUser(user2);

        // then
        assertThat(reservationsByUser).asList().isEmpty();
    }

    @Test
    void itShouldFindReservationsByUser() {
        // given
        User user1 = new User("username1", "password1", roleRepository.findByName("USER"));
        user1 = userRepository.save(user1);
        User user2 = new User("username2", "password1", roleRepository.findByName("USER"));
        user2 = userRepository.save(user2);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1a = new Reservation(start, end, 1, user1, bowlingLane);
        underTest.save(reservation1a);

        start = LocalDateTime.of(3000, 2, 1, 8, 0);
        end = LocalDateTime.of(3000, 2, 1, 9, 0);
        Reservation reservation1b = new Reservation(start, end, 1, user1, bowlingLane);
        underTest.save(reservation1b);

        start = LocalDateTime.of(3000, 3, 1, 8, 0);
        end = LocalDateTime.of(3000, 3, 1, 9, 0);
        Reservation reservation2a = new Reservation(start, end, 1, user2, bowlingLane);
        underTest.save(reservation2a);

        start = LocalDateTime.of(3000, 4, 1, 8, 0);
        end = LocalDateTime.of(3000, 4, 1, 9, 0);
        Reservation reservation2b = new Reservation(start, end, 1, user2, bowlingLane);
        underTest.save(reservation2b);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByUser(user1);

        // then
        assertThat(reservationsByUser).asList().containsExactlyInAnyOrder(reservation1a, reservation1b);
    }

    @Test
    @Disabled
    void findReservationsByOverlap() {
    }

    @Test
    @Disabled
    void findReservationsByBowlingLane() {
    }
}