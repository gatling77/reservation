package ch.corner.envres.repository;

import ch.corner.envres.domain.Appl;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Appl entity.
 */
public interface ApplRepository extends JpaRepository<Appl,Long> {

    @Query("select distinct appl from Appl appl left join fetch appl.compatibleEnvironmentss")
    List<Appl> findAllWithEagerRelationships();

    @Query("select appl from Appl appl left join fetch appl.compatibleEnvironmentss where appl.id =:id")
    Appl findOneWithEagerRelationships(@Param("id") Long id);

}
