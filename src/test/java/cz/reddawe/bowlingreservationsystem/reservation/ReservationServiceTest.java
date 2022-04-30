package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLaneService;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ReservationDeletionTimeExpiredException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ReservationInvalidException;
import cz.reddawe.bowlingreservationsystem.exceptions.badrequest.ResourceDoesNotExistException;
import cz.reddawe.bowlingreservationsystem.exceptions.forbidden.ForbiddenException;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationInput;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithIsMineFlag;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithoutUser;
import cz.reddawe.bowlingreservationsystem.user.User;
import cz.reddawe.bowlingreservationsystem.user.UserService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private ReservationService underTest;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserService userService;
    @Mock
    private BowlingLaneService bowlingLaneService;

    @BeforeEach
    void setUp() {
        underTest = new ReservationService(reservationRepository, userService, bowlingLaneService);
    }

    @Test
    void itShouldGetAllReservations() {
        // given
        BowlingLane bowlingLane = new BowlingLane(1);
        User user1 = new User();
        ReflectionTestUtils.setField(user1, "id", 1L);
        User user2 = new User();
        ReflectionTestUtils.setField(user2, "id", 2L);

        LocalDateTime start1 = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end1 = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start1, end1,
                1, user1, bowlingLane);
        ReflectionTestUtils.setField(reservation1, "id", 1L);

        LocalDateTime start2 = LocalDateTime.of(3000, 2, 1, 8, 0);
        LocalDateTime end2 = LocalDateTime.of(3000, 2, 1, 9, 0);
        Reservation reservation2 = new Reservation(start2, end2,
                1, user2, bowlingLane);
        ReflectionTestUtils.setField(reservation2, "id", 2L);

        given(reservationRepository.findAll()).willReturn(List.of(reservation1, reservation2));

        given(userService.getCurrentUser()).willReturn(Optional.of(user1));

        // when
        List<ReservationWithIsMineFlag> result = underTest.getAllReservations();

        // then
        assertThat(result).asList().containsExactlyInAnyOrder(
                new ReservationWithIsMineFlag(1L, start1, end1, 1, true, bowlingLane),
                new ReservationWithIsMineFlag(2L, start2, end2, 1, false, bowlingLane)
        );
    }

    @Test
    void itShouldGetMyReservations() {
        // given
        BowlingLane bowlingLane = new BowlingLane(1);
        User user1 = new User();
        ReflectionTestUtils.setField(user1, "id", 1L);
        User user2 = new User();
        ReflectionTestUtils.setField(user2, "id", 2L);

        LocalDateTime start1 = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end1 = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation1 = new Reservation(start1, end1,
                1, user1, bowlingLane);
        ReflectionTestUtils.setField(reservation1, "id", 1L);

        LocalDateTime start2 = LocalDateTime.of(3000, 2, 1, 8, 0);
        LocalDateTime end2 = LocalDateTime.of(3000, 2, 1, 9, 0);
        Reservation reservation2 = new Reservation(start2, end2,
                1, user2, bowlingLane);
        ReflectionTestUtils.setField(reservation2, "id", 2L);

        given(reservationRepository.findReservationsByUser(user1)).willReturn(List.of(reservation1));

        given(userService.getCurrentUser()).willReturn(Optional.of(user1));

        // when
        List<ReservationWithoutUser> result = underTest.getMyReservations();

        // then
        assertThat(result).asList().containsExactly(
                new ReservationWithoutUser(1L, start1, end1, 1, bowlingLane)
        );
    }

    @Test
    void itShouldThrowIfGetMyReservationsCalledByAnonymousUser() {
        // given
        given(userService.getCurrentUser()).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getMyReservations())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("getMyReservations can only be called by an authenticated user");
    }

    @Test
    void itShouldCreateReservation() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane(1);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        ReservationInput reservationInput = new ReservationInput(start, end, 1, bowlingLane);

        Reservation reservationReturn = new Reservation(start, end, 1, user, bowlingLane);
        ReflectionTestUtils.setField(reservationReturn, "id", 1L);

        given(bowlingLaneService.doesBowlingLaneExist(1)).willReturn(true);
        given(reservationRepository.findReservationsByOverlap(any(), any(), any()))
                .willReturn(Collections.emptyList());
        given(userService.getCurrentUser()).willReturn(Optional.of(user));
        given(reservationRepository.save(any())).willReturn(reservationReturn);

        // when
        ReservationWithoutUser result = underTest.createReservation(reservationInput);

        // then
        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationArgumentCaptor.capture());

        Reservation reservationArgument = new Reservation(start, end, 1, user, bowlingLane);
        assertThat(reservationArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(reservationArgument);

        assertThat(result).isEqualTo(new ReservationWithoutUser(1L, start, end, 1, bowlingLane));
    }

    @Test
    void itShouldThrowAtInvalidBowlingLane() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane(1);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        ReservationInput reservationInput = new ReservationInput(start, end, 1, bowlingLane);

        given(bowlingLaneService.doesBowlingLaneExist(1)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.createReservation(reservationInput))
                .isInstanceOf(ReservationInvalidException.class)
                .matches(e -> ((ReservationInvalidException) e).getReason().equals("bowlingLane"));
    }

    @Test
    void itShouldThrowAtZeroPeopleComing() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane(1);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        ReservationInput reservationInput = new ReservationInput(start, end, 0, bowlingLane);

        given(bowlingLaneService.doesBowlingLaneExist(1)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.createReservation(reservationInput))
                .isInstanceOf(ReservationInvalidException.class)
                .matches(e -> ((ReservationInvalidException) e).getReason().equals("peopleComing"));
    }

    @Test
    void itShouldThrowAtNegativePeopleComing() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane(1);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        ReservationInput reservationInput = new ReservationInput(start, end, -420, bowlingLane);

        given(bowlingLaneService.doesBowlingLaneExist(1)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.createReservation(reservationInput))
                .isInstanceOf(ReservationInvalidException.class)
                .matches(e -> ((ReservationInvalidException) e).getReason().equals("peopleComing"));
    }

    @Test
    void itShouldThrowAtStartEqualToEnd() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane(1);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 8, 0);
        ReservationInput reservationInput = new ReservationInput(start, end, 1, bowlingLane);

        given(bowlingLaneService.doesBowlingLaneExist(1)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.createReservation(reservationInput))
                .isInstanceOf(ReservationInvalidException.class)
                .matches(e -> ((ReservationInvalidException) e).getReason().equals("start>=end"));
    }

    @Test
    void itShouldThrowAtStartAfterEnd() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane(1);

        LocalDateTime start = LocalDateTime.of(3000, 10, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 8, 0);
        ReservationInput reservationInput = new ReservationInput(start, end, 1, bowlingLane);

        given(bowlingLaneService.doesBowlingLaneExist(1)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.createReservation(reservationInput))
                .isInstanceOf(ReservationInvalidException.class)
                .matches(e -> ((ReservationInvalidException) e).getReason().equals("start>=end"));
    }

    @Test
    void itShouldThrowAtOverlap() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane(1);

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        ReservationInput reservationInput = new ReservationInput(start, end, 1, bowlingLane);

        given(bowlingLaneService.doesBowlingLaneExist(1)).willReturn(true);
        given(reservationRepository.findReservationsByOverlap(any(), any(), any())).willReturn(List.of(
                new Reservation()
        ));

        // when
        // then
        assertThatThrownBy(() -> underTest.createReservation(reservationInput))
                .isInstanceOf(ReservationInvalidException.class)
                .matches(e -> ((ReservationInvalidException) e).getReason().equals("overlap"));
    }

    @Test
    void itShouldDeleteReservation() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane();

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation = new Reservation(start, end, 1, user, bowlingLane);

        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));
        given(userService.getCurrentUser()).willReturn(Optional.of(user));

        // when
        underTest.deleteReservation(1);

        // then
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(reservationRepository).deleteById(longArgumentCaptor.capture());
        assertThat(longArgumentCaptor.getValue()).isEqualTo(1);
    }

    @Test
    void itShouldThrowIfCalledOnWrongId() {
        // given
        given(reservationRepository.findById(1L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReservation(1))
                .isInstanceOf(ResourceDoesNotExistException.class)
                .matches(e -> ((ResourceDoesNotExistException) e).getReason().equals("1"));
    }

    @Test
    void itShouldThrowIfDeleteReservationCalledByAnonymousUser() {
        // given
        given(reservationRepository.findById(1L)).willReturn(Optional.of(new Reservation()));
        given(userService.getCurrentUser()).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReservation(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("deleteReservation can only be called by an authenticated user");
    }

    @Test
    void itShouldThrowIfCalledByDifferentUser() {
        // given
        User user1 = new User();
        ReflectionTestUtils.setField(user1, "id", 1L);
        User user2 = new User();
        ReflectionTestUtils.setField(user2, "id", 2L);


        BowlingLane bowlingLane = new BowlingLane();

        LocalDateTime start = LocalDateTime.of(3000, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(3000, 1, 1, 9, 0);
        Reservation reservation = new Reservation(start, end, 1, user1, bowlingLane);

        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));
        given(userService.getCurrentUser()).willReturn(Optional.of(user2));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReservation(1))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You attempted to delete someone else's reservation");
    }

    @Test
    void itShouldThrowWhenTimeExpired() {
        // given
        User user = new User();
        BowlingLane bowlingLane = new BowlingLane();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.MAX;
        Reservation reservation = new Reservation(start, end, 1, user, bowlingLane);

        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));
        given(userService.getCurrentUser()).willReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReservation(1))
                .isInstanceOf(ReservationDeletionTimeExpiredException.class)
                .matches(e -> ((ReservationDeletionTimeExpiredException) e).getReason().equals(start.toString()));
    }
}