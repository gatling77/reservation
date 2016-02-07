package ch.corner.envres.repository.search;

import ch.corner.envres.domain.Environment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Environment entity.
 */
public interface EnvironmentSearchRepository extends ElasticsearchRepository<Environment, Long> {
}
