package cz.reddawe.bowlingreservationsystem.authorization;

import javax.persistence.*;
import java.util.List;

/**
 * Represents an authority.
 *
 * @author David Dvorak
 */
@Entity(name = "Authority")
@Table(name = "authorities")
public class Authority {

    @SequenceGenerator(
            name = "authority_sequence",
            sequenceName = "authority_sequence",
            allocationSize = 1
    )

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "authority_sequence"
    )
    @JoinColumn(
            name = "id",
            columnDefinition = "bigint",
            updatable = false
    )
    private Long id;

    @Column(
            name = "name",
            columnDefinition = "varchar(255)",
            unique = true,
            nullable = false
    )
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private List<Role> roles;

    public Authority() {
    }

    public Authority(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
