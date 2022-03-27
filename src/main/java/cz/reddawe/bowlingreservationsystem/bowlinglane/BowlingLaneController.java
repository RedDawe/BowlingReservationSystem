package cz.reddawe.bowlingreservationsystem.bowlinglane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/bowling-lane")
public class BowlingLaneController {

    private final BowlingLaneService bowlingLaneService;

    @Autowired
    public BowlingLaneController(BowlingLaneService bowlingLaneService) {
        this.bowlingLaneService = bowlingLaneService;
    }

    @GetMapping(path = "get")
    public List<BowlingLane> getBowlingLanes() {
        return bowlingLaneService.getBowlingLanes();
    }

    @PostMapping(path = "add")
    public ResponseEntity<BowlingLane> addBowlingLane(@RequestBody BowlingLane bowlingLane) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/bowling-lane/add")
                .toUriString());
        return ResponseEntity.created(uri).body(bowlingLaneService.addBowlingLane(bowlingLane));
    }

    @DeleteMapping(path = "remove")
    public void removeBowlingLane(@RequestBody BowlingLane bowlingLane) {
        bowlingLaneService.removeBowlingLane(bowlingLane);
    }
}
