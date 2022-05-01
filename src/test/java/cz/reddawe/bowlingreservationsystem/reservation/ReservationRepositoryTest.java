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
        User user = new User("username", "password1", roleRepository.getByName("USER"));
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
        User user1 = new User("username1", "password1", roleRepository.getByName("USER"));
        user1 = userRepository.save(user1);
        User user2 = new User("username2", "password1", roleRepository.getByName("USER"));
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
        User user1 = new User("username1", "password1", roleRepository.getByName("USER"));
        user1 = userRepository.save(user1);
        User user2 = new User("username2", "password1", roleRepository.getByName("USER"));
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
    void itShouldNotFindReservationsByOverlap() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 8, 30);
        Reservation reservation1 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation1);

        start = LocalDateTime.of(3000, 1, 1, 10, 0);
        end = LocalDateTime.of(3000, 1, 1, 11, 0);
        Reservation reservation2 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation2);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByOverlap(
                LocalDateTime.of(3000, 1, 1, 12, 59),
                LocalDateTime.of(3000, 1, 1, 18, 0),
                bowlingLane
        );

        // then
        assertThat(reservationsByUser).asList().isEmpty();
    }

    @Test
    void itShouldNotFindReservationsByOverlapBorderline() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation1);

        start = LocalDateTime.of(3000, 1, 1, 10, 0);
        end = LocalDateTime.of(3000, 1, 1, 11, 0);
        Reservation reservation2 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation2);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByOverlap(
                LocalDateTime.of(3000, 1, 1, 9, 0),
                LocalDateTime.of(3000, 1, 1, 10, 0),
                bowlingLane
        );

        // then
        assertThat(reservationsByUser).asList().isEmpty();
    }

    @Test
    void itShouldFindReservationsByOverlapPartialStart() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation1);

        start = LocalDateTime.of(3000, 1, 1, 13, 0);
        end = LocalDateTime.of(3000, 1, 1, 14, 0);
        Reservation reservation2 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation2);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByOverlap(
                LocalDateTime.of(3000, 1, 1, 8, 25),
                LocalDateTime.of(3000, 1, 1, 12, 1),
                bowlingLane
        );

        // then
        assertThat(reservationsByUser).asList().containsExactlyInAnyOrder(reservation1);
    }

    @Test
    void itShouldFindReservationsByOverlapPartialEnd() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 8, 7);
        Reservation reservation1 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation1);

        start = LocalDateTime.of(3000, 1, 1, 12, 0);
        end = LocalDateTime.of(3000, 1, 1, 14, 0);
        Reservation reservation2 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation2);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByOverlap(
                LocalDateTime.of(3000, 1, 1, 8, 25),
                LocalDateTime.of(3000, 1, 1, 12, 1),
                bowlingLane
        );

        // then
        assertThat(reservationsByUser).asList().containsExactlyInAnyOrder(reservation2);
    }

    @Test
    void itShouldFindReservationsByOverlapsPartial() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation1);

        start = LocalDateTime.of(3000, 1, 1, 12, 0);
        end = LocalDateTime.of(3000, 1, 1, 14, 0);
        Reservation reservation2 = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation2);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByOverlap(
                LocalDateTime.of(3000, 1, 1, 8, 25),
                LocalDateTime.of(3000, 1, 1, 12, 1),
                bowlingLane
        );

        // then
        assertThat(reservationsByUser).asList().containsExactlyInAnyOrder(reservation1, reservation2);
    }

    @Test
    void itShouldFindReservationByOverlapSuperset() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 11, 0);
        Reservation reservation = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByOverlap(
                LocalDateTime.of(3000, 1, 1, 9, 0),
                LocalDateTime.of(3000, 1, 1, 10, 0),
                bowlingLane
        );

        // then
        assertThat(reservationsByUser).asList().containsExactly(reservation);
    }

    @Test
    void itShouldFindReservationByOverlapSubset() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 15, 0);
        end = LocalDateTime.of(3000, 1, 1, 16, 0);
        Reservation reservation = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByOverlap(
                LocalDateTime.of(3000, 1, 1, 9, 0),
                LocalDateTime.of(3000, 1, 1, 19, 0),
                bowlingLane
        );

        // then
        assertThat(reservationsByUser).asList().containsExactly(reservation);
    }

    @Test
    void itShouldFindReservationsByBowlingLane() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane1 = new BowlingLane(1);
        bowlingLane1 = bowlingLaneRepository.save(bowlingLane1);
        BowlingLane bowlingLane2 = new BowlingLane(2);
        bowlingLane2 = bowlingLaneRepository.save(bowlingLane2);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start, end, 1, user, bowlingLane1);
        underTest.save(reservation1);

        start = LocalDateTime.of(3000, 2, 1, 8, 0);
        end = LocalDateTime.of(3000, 2, 1, 9, 0);
        Reservation reservation2 = new Reservation(start, end, 1, user, bowlingLane2);
        underTest.save(reservation2);

        // when
        List<Reservation> reservationsByUser = underTest.findReservationsByBowlingLane(bowlingLane1);

        // then
        assertThat(reservationsByUser).asList().containsExactly(reservation1);
    }

    @Test
    void itShouldFindIdByExpired() {
        // given
        User user = new User("username1", "password1", roleRepository.getByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start, end, 1, user, bowlingLane);
        reservation1 = underTest.save(reservation1);

        start = LocalDateTime.of(3000, 2, 1, 8, 0);
        end = LocalDateTime.of(3000, 2, 1, 9, 0);
        Reservation reservation2 = new Reservation(start, end, 1, user, bowlingLane);
        reservation2 = underTest.save(reservation2);

        // when
        List<Long> result = underTest.findIdByExpired(LocalDateTime.of(
                3000, 2,  1, 8, 30));

        // then
        assertThat(result).asList().containsExactly(reservation1.getId());
    }
}