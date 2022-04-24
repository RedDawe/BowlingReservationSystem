package cz.reddawe.bowlingreservationsystem.user;

import javax.persistence.*;
import java.util.List;

@Entity(name = "Role")
@Table(name = "roles")
public class Role {

    @SequenceGenerator(
            name = "role_sequence",
            sequenceName = "role_sequence",
            allocationSize = 1
    )

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "role_sequence"
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

    @ManyToMany
    @JoinTable(
            name = "roles_have_authorities"
    )
    private List<Authority> authorities;

    public Role() {
    }

    public Role(String name, List<Authority> authorities) {
        this.name = name;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
