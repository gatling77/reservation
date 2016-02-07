package ch.corner.envres.repository;

import ch.corner.envres.domain.Environment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Environment entity.
 */
public interface EnvironmentRepository extends JpaRepository<Environment,Long> {

}
