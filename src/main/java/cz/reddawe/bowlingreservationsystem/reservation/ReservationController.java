package cz.reddawe.bowlingreservationsystem.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping(path = "get-my")
    public List<Reservation> getMyReservations() {
        return reservationService.getMyReservations();
    }

    @PostMapping(path = "create")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/reservation/create")
                .toUriString());
        return ResponseEntity.created(uri).body(reservationService.createReservation(reservation));
    }

    @DeleteMapping(path = "delete/{reservationId}")
    public void deleteReservation(@PathVariable("reservationId") long reservationId) {
        reservationService.deleteReservation(reservationId);
    }
}
