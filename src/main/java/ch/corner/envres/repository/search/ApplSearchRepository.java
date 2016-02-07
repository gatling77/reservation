package ch.corner.envres.repository.search;

import ch.corner.envres.domain.Appl;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Appl entity.
 */
public interface ApplSearchRepository extends ElasticsearchRepository<Appl, Long> {
}
