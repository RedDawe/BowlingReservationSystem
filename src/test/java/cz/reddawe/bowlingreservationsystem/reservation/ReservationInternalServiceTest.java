package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.authorization.Role;
import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithUsername;
import cz.reddawe.bowlingreservationsystem.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationInternalServiceTest {

    private ReservationInternalService underTest;
    @Mock
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        underTest = new ReservationInternalService(reservationRepository);
    }

    @Test
    void itShouldGetReservationsByLane() {
        // given
        BowlingLane bowlingLane = new BowlingLane(1);
        User user = new User("username", "password1", new Role());

        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.of(3000, 1, 1, 8, 0);
        end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start, end,
                1, user, bowlingLane);
        ReflectionTestUtils.setField(reservation1, "id", 1L);
        ReservationWithUsername reservationWithUsername = new ReservationWithUsername(1L, start, end, 1,
                "username", bowlingLane);

        given(reservationRepository.findReservationsByBowlingLane(bowlingLane)).willReturn(List.of(reservation1));

        // when
        List<ReservationWithUsername> result = underTest.getReservationsByLane(bowlingLane);

        // then
        assertThat(result).asList().containsExactly(reservationWithUsername);
    }

    @Test
    void itShouldDeleteReservation() {
        // given
        given(reservationRepository.existsById(1L)).willReturn(true);

        // when
        underTest.forcefullyDeleteReservation(1);

        // then
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(reservationRepository).deleteById(longArgumentCaptor.capture());
        assertThat(longArgumentCaptor.getValue()).isEqualTo(1);
    }

    @Test
    void itShouldThrowIfInvalidIdDuringDeletion() {
        // given
        given(reservationRepository.existsById(1L)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.forcefullyDeleteReservation(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Reservation 1 does not exist");
    }

    @Test
    void itShouldChangeBowlingLane() {
        // given
        BowlingLane bowlingLane = new BowlingLane(2);
        Reservation reservation = Mockito.mock(Reservation.class);

        given(reservationRepository.findById(5L)).willReturn(Optional.of(reservation));

        // when
        underTest.changeBowlingLane(5, bowlingLane);

        // then
        ArgumentCaptor<BowlingLane> bowlingLaneArgumentCaptor = ArgumentCaptor.forClass(BowlingLane.class);
        verify(reservation).setBowlingLane(bowlingLaneArgumentCaptor.capture());
        assertThat(bowlingLaneArgumentCaptor.getValue()).isEqualTo(bowlingLane);
    }

    @Test
    void itShouldThrowIfInvalidIdDuringChangingBowlingLane() {
        // given
        given(reservationRepository.findById(any())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.changeBowlingLane(30, new BowlingLane()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Reservation 30 does not exist");
    }
}