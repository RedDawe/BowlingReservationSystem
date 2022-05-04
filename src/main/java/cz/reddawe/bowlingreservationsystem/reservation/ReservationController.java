package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationInput;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithIsMineFlag;
import cz.reddawe.bowlingreservationsystem.reservation.iorecords.ReservationWithoutUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for Reservation entity.
 *
 * @author David Dvorak
 */
@RestController
@RequestMapping(path = "api/v1/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Returns all reservations",
            description = """
                    Reservations of the currently logged-in user have isMine field set to true.
                    If no user is logged-in all reservation have isMine equal to false.""",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ReservationWithIsMineFlag.class))
                    ))
            }
    )
    @GetMapping(path = "get-all")
    public List<ReservationWithIsMineFlag> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(
            summary = "Returns reservations of the currently logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ReservationWithoutUser.class))
                    ))
            },
            security = @SecurityRequirement(name = "Authenticated")
    )
    @GetMapping(path = "get-my")
    public List<ReservationWithoutUser> getMyReservations() {
        return reservationService.getMyReservations();
    }

    @Operation(
            summary = "Creates a new reservation",
            description = """
                    Requires:
                     1. bowlingLane that is present in the database
                     2. peopleComing bigger than 0
                     3. start strictly smaller than end
                     4. no overlap with another reservation on the same bowlingLane
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReservationWithoutUser.class)
                    )),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "403")
            },
            security = @SecurityRequirement(name = "RESERVATION:CREATE")
    )
    @PostMapping(path = "create")
    public ResponseEntity<ReservationWithoutUser> createReservation(@RequestBody ReservationInput reservationInput) {
        return ResponseEntity.ok().body(reservationService.createReservation(reservationInput));
    }

    @Operation(
            summary = "Deletes a reservation",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "403")
            },
            security = @SecurityRequirement(name = "RESERVATION:DELETE")
    )
    @DeleteMapping(path = "delete/{reservationId}")
    public void deleteReservation(@PathVariable("reservationId") long reservationId) {
        reservationService.deleteReservation(reservationId);
    }
}
