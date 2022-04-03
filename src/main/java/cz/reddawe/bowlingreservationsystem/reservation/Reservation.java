package cz.reddawe.bowlingreservationsystem.reservation;

import cz.reddawe.bowlingreservationsystem.bowlinglane.BowlingLane;
import cz.reddawe.bowlingreservationsystem.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

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
            columnDefinition = "time",
            nullable = false
    )
    private LocalTime start;

    @Column(
            name = "reservation_end",
            columnDefinition = "time",
            nullable = false
    )
    private LocalTime end;

    @Column(
            name = "reservation_date",
            columnDefinition = "date",
            nullable = false
    )
    private LocalDate date;

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

    public Long getId() {
        return id;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}
