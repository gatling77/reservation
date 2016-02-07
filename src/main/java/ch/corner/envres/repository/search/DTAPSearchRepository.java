package ch.corner.envres.repository.search;

import ch.corner.envres.domain.DTAP;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the DTAP entity.
 */
public interface DTAPSearchRepository extends ElasticsearchRepository<DTAP, Long> {
}
