package cz.reddawe.bowlingreservationsystem.bowlinglane;

import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceAlreadyExistsException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceDoesNotExistException;
import cz.reddawe.bowlingreservationsystem.reservation.Reservation;
import cz.reddawe.bowlingreservationsystem.reservation.ReservationInternalService;
import cz.reddawe.bowlingreservationsystem.reservation.ReservationService;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithUsername;
import cz.reddawe.bowlingreservationsystem.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BowlingLaneServiceTest {

    @Mock
    private ReservationInternalService reservationInternalService;
    @Mock
    private BowlingLaneRepository bowlingLaneRepository;
    private BowlingLaneService underTest;

    @BeforeEach
    void setUp() {
        underTest = new BowlingLaneService(reservationInternalService, bowlingLaneRepository);
    }

    @Test
    void itShouldCreateBowlingLane() {
        // given
        BowlingLane bowlingLane = new BowlingLane(1);
        BowlingLane willReturnBowlingLane = new BowlingLane(1);

        given(bowlingLaneRepository.save(bowlingLane)).willReturn(willReturnBowlingLane);

        // when
        BowlingLane createdBowlingLane = underTest.createBowlingLane(bowlingLane);

        // then
        ArgumentCaptor<BowlingLane> bowlingLaneArgumentCaptor = ArgumentCaptor.forClass(BowlingLane.class);
        verify(bowlingLaneRepository).save(bowlingLaneArgumentCaptor.capture());
        BowlingLane capturedBowlingLane = bowlingLaneArgumentCaptor.getValue();

        assertThat(capturedBowlingLane).isEqualTo(bowlingLane);
        assertThat(createdBowlingLane).isEqualTo(willReturnBowlingLane);
    }

    @Test
    void itShouldThrowWhenBowlingLaneExists() {
        // given
        BowlingLane bowlingLane = new BowlingLane(1);

        given(bowlingLaneRepository.existsById(1)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.createBowlingLane(bowlingLane))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .matches(e -> ((ResourceAlreadyExistsException) e).getReason().equals("1"));

        verify(bowlingLaneRepository, never()).save(any());
    }

    @Test
    void itShouldDeleteBowlingLaneAndReassignReservation() {
        // given
        BowlingLane toBeRemoved = new BowlingLane(1);
        BowlingLane alternative = new BowlingLane(2);

        User user = new User();

        LocalDateTime shouldReassignStart = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime shouldReassignEnd = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation shouldReassignReservation = new Reservation(shouldReassignStart, shouldReassignEnd,
                1, user, toBeRemoved);
        ReflectionTestUtils.setField(shouldReassignReservation, "id", 1L);
        ReservationWithUsername shouldReassignReservationWithUsername = new ReservationWithUsername(
                1L, shouldReassignStart, shouldReassignEnd, 1, "username1", toBeRemoved);

        LocalDateTime cannotReassignStart = LocalDateTime.of(3000, 2, 1, 8, 0);
        LocalDateTime cannotReassignEnd = LocalDateTime.of(3000, 2, 1, 9, 0);
        Reservation cannotReassignReservation = new Reservation(cannotReassignStart, cannotReassignEnd,
                1, user, toBeRemoved);
        ReflectionTestUtils.setField(cannotReassignReservation, "id", 2L);
        ReservationWithUsername cannotReassignReservationWithUsername = new ReservationWithUsername(
                2L, cannotReassignStart, cannotReassignEnd, 1, "username2", toBeRemoved);

        given(bowlingLaneRepository.findById(1)).willReturn(Optional.of(toBeRemoved));
        given(reservationInternalService.getReservationsByLane(toBeRemoved)).willReturn(List.of(
                shouldReassignReservationWithUsername, cannotReassignReservationWithUsername
        ));

        given(bowlingLaneRepository.findAlternativeBowlingLaneFor(shouldReassignStart, shouldReassignEnd, toBeRemoved))
                .willReturn(Collections.singletonList(alternative));
        given(bowlingLaneRepository.findAlternativeBowlingLaneFor(cannotReassignStart, cannotReassignEnd, toBeRemoved))
                .willReturn(Collections.emptyList());

        // when
        List<String> result = underTest.deleteBowlingLane(toBeRemoved.getNumber());

        // then
        ArgumentCaptor<Long> shouldReassignIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<BowlingLane> bowlingLaneArgumentCaptor = ArgumentCaptor.forClass(BowlingLane.class);
        verify(reservationInternalService).changeBowlingLane(shouldReassignIdArgumentCaptor.capture(),
                bowlingLaneArgumentCaptor.capture());
        assertThat(shouldReassignIdArgumentCaptor.getValue()).isEqualTo(1);
        assertThat(bowlingLaneArgumentCaptor.getValue()).isEqualTo(alternative);

        ArgumentCaptor<Long> cannotReassignIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(reservationInternalService).forcefullyDeleteReservation(cannotReassignIdArgumentCaptor.capture());
        assertThat(cannotReassignIdArgumentCaptor.getValue()).isEqualTo(2);

        assertThat(result).asList().containsExactlyInAnyOrder(cannotReassignReservationWithUsername.toString());
    }

    @Test
    void itShouldThrowWhenBowlingLaneDoesNotExist() {
        // given
        given(bowlingLaneRepository.findById(1)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteBowlingLane(1))
                .isInstanceOf(ResourceDoesNotExistException.class)
                .matches(e -> ((ResourceDoesNotExistException) e).getReason().equals("1"));
    }

    @Test
    void itShouldReturnBowlingLanesOrdered() {
        // given
        List<BowlingLane> bowlingLanes = List.of(new BowlingLane(23), new BowlingLane(7));

        given(bowlingLaneRepository.findAllByOrderByNumber()).willReturn(bowlingLanes);

        // when
        List<BowlingLane> result = underTest.getBowlingLanesOrdered();

        // then
        assertThat(result).isEqualTo(bowlingLanes);
    }
}