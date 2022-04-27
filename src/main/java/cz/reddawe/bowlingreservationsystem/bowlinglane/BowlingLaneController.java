package cz.reddawe.bowlingreservationsystem.bowlinglane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return bowlingLaneService.getBowlingLanesOrdered();
    }

    @PostMapping(path = "create")
    public ResponseEntity<BowlingLane> createBowlingLane(@RequestBody BowlingLane bowlingLane) {
        return ResponseEntity.ok().body(bowlingLaneService.createBowlingLane(bowlingLane));
    }

    @DeleteMapping(path = "delete/{number}")
    public List<String> deleteBowlingLane(@PathVariable("number") int number) {
        return bowlingLaneService.deleteBowlingLane(number);
    }
}
