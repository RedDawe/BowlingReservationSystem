package cz.reddawe.bowlingreservationsystem.authorization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for Role entity.
 *
 * @author David Dvorak
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("FROM Role r WHERE  r.name = ?1")
    Role getByName(String name);
}
