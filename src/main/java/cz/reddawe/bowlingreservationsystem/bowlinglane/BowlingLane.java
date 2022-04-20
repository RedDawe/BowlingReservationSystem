package cz.reddawe.bowlingreservationsystem.bowlinglane;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity(name = "BowlingLane")
@Table(name = "bowling_lanes")
public class BowlingLane {
    @Id
    @JoinColumn(
            name = "number",
            columnDefinition = "int",
            updatable = false
    )
    private Integer number;

    public BowlingLane() {
    }

    public BowlingLane(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "BowlingLane{" +
                "number=" + number +
                '}';
    }
}
