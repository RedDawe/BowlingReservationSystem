package cz.reddawe.bowlingreservationsystem.authorization;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Authority entity.
 *
 * @author David Dvorak
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
