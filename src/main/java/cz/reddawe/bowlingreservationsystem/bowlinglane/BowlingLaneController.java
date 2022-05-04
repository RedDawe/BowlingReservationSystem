package cz.reddawe.bowlingreservationsystem.bowlinglane;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for BowlingLane entity.
 *
 * @author David Dvorak
 */
@RestController
@RequestMapping("api/v1/bowling-lane")
public class BowlingLaneController {

    private final BowlingLaneService bowlingLaneService;

    @Autowired
    public BowlingLaneController(BowlingLaneService bowlingLaneService) {
        this.bowlingLaneService = bowlingLaneService;
    }

    @Operation(
            summary = "Returns all bowling lanes ordered by number",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = BowlingLane.class))
                    ))
            }
    )
    @GetMapping(path = "get")
    public List<BowlingLane> getBowlingLanesOrdered() {
        return bowlingLaneService.getBowlingLanesOrdered();
    }

    @Operation(
            summary = "Creates a new bowling lane",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BowlingLane.class)
                    )),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "403")
            },
            security = @SecurityRequirement(name = "BOWLING_LANE:CREATE")
    )
    @PostMapping(path = "create")
    public ResponseEntity<BowlingLane> createBowlingLane(@RequestBody BowlingLane bowlingLane) {
        return ResponseEntity.ok().body(bowlingLaneService.createBowlingLane(bowlingLane));
    }

    @Operation(
            summary = "Deletes a bowling lane",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )),
                    @ApiResponse(responseCode = "400"),
                    @ApiResponse(responseCode = "403")
            },
            security = @SecurityRequirement(name = "BOWLING_LANE:DELETE")
    )
    @DeleteMapping(path = "delete/{number}")
    public List<String> deleteBowlingLane(@PathVariable("number") int number) {
        return bowlingLaneService.deleteBowlingLane(number);
    }
}
