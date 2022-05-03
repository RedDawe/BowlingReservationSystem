package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a reservation.
 *
 * @author David Dvorak
 */
@Entity(name = "Reservation")
@Table(name = "reservations")
public class Reservation {

    @SequenceGenerator(
            name = "reservation_sequence",
            sequenceName = "reservation_sequence",
            allocationSize = 1
    )

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reservation_sequence"
    )
    @Column(
            name = "id",
            columnDefinition = "bigint",
            updatable = false
    )
    private Long id;

    @Column(
            name = "reservation_start",
            columnDefinition = "timestamp without time zone",
            nullable = false
    )
    private LocalDateTime start;

    @Column(
            name = "reservation_end",
            columnDefinition = "timestamp without time zone",
            nullable = false
    )
    private LocalDateTime end;

    @Column(
            name = "people_coming",
            columnDefinition = "int",
            nullable = false
    )
    private int peopleComing;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private BowlingLane bowlingLane;

    public Reservation() {
    }

    public Reservation(LocalDateTime start, LocalDateTime end, int peopleComing,
                       User user, BowlingLane bowlingLane) {
        this.start = start;
        this.end = end;
        this.peopleComing = peopleComing;
        this.user = user;
        this.bowlingLane = bowlingLane;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getPeopleComing() {
        return peopleComing;
    }

    public void setPeopleComing(int peopleComing) {
        this.peopleComing = peopleComing;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BowlingLane getBowlingLane() {
        return bowlingLane;
    }

    public void setBowlingLane(BowlingLane bowlingLane) {
        this.bowlingLane = bowlingLane;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", peopleComing=" + peopleComing +
                ", user=" + user +
                ", bowlingLane=" + bowlingLane +
                '}';
    }
}
