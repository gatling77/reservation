package ch.corner.envres.repository;

import ch.corner.envres.domain.DTAP;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DTAP entity.
 */
public interface DTAPRepository extends JpaRepository<DTAP,Long> {

}
