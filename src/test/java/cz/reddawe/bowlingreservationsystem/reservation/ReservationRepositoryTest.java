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

        authorityRepository.saveAll(List.of(
                reservationCreate, reservationDelete,
                bowlingLaneCreate, bowlingLaneDelete
        ));

        Role user = new Role("USER", List.of(
                reservationCreate, reservationDelete
        ));
        Role manager = new Role("MANAGER", List.of(
                bowlingLaneCreate, bowlingLaneDelete
        ));

        roleRepository.saveAll(List.of(
                user,
                manager
        ));
    }

    @Test
    void itShouldFindReservationsByUser() {
        //given
        User user = new User("username", "password1", roleRepository.findByName("USER"));
        user = userRepository.save(user);

        BowlingLane bowlingLane = new BowlingLane(1);
        bowlingLane = bowlingLaneRepository.save(bowlingLane);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation = new Reservation(start, end, 1, user, bowlingLane);
        underTest.save(reservation);

        //when
        List<Reservation> reservationsByUser = underTest.findReservationsByUser(user);

        //then
        assertThat(reservationsByUser).asList().containsExactly(reservation);
    }

    @Test
    void findReservationsByOverlap() {
    }

    @Test
    void findReservationsByBowlingLane() {
    }
}