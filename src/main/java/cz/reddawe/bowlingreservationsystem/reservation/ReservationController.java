package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationInput;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithIsMineFlag;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithoutUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(path = "get-all")
    public List<ReservationWithIsMineFlag> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping(path = "get-my")
    public List<ReservationWithoutUser> getMyReservations() {
        return reservationService.getMyReservations();
    }

    @PostMapping(path = "create")
    public ResponseEntity<ReservationWithoutUser> createReservation(@RequestBody ReservationInput reservationInput) {
        return ResponseEntity.ok().body(reservationService.createReservation(reservationInput));
    }

    @DeleteMapping(path = "delete/{reservationId}")
    public void deleteReservation(@PathVariable("reservationId") long reservationId) {
        reservationService.deleteReservation(reservationId);
    }
}
