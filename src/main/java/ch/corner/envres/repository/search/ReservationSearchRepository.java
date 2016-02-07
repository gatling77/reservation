package ch.corner.envres.repository.search;

import ch.corner.envres.domain.Reservation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Reservation entity.
 */
public interface ReservationSearchRepository extends ElasticsearchRepository<Reservation, Long> {
}
