package ch.corner.envres.repository;

import ch.corner.envres.domain.Reservation;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Reservation entity.
 */
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    @Query("select reservation from Reservation reservation where reservation.requestor.login = ?#{principal.username}")
    List<Reservation> findByRequestorIsCurrentUser();

    @Query("select reservation from Reservation reservation where reservation.appl.id = ?1 and reservation.environment.id = ?2")
    List<Reservation> findByApplAndEnvironment(Long applId, Long environmentId);

}
