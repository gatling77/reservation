package ch.corner.envres.web.rest;

import com.codahale.metrics.annotation.Timed;
import ch.corner.envres.domain.Environment;
import ch.corner.envres.repository.EnvironmentRepository;
import ch.corner.envres.repository.search.EnvironmentSearchRepository;
import ch.corner.envres.web.rest.util.HeaderUtil;
import ch.corner.envres.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Environment.
 */
@RestController
@RequestMapping("/api")
public class EnvironmentResource {

    private final Logger log = LoggerFactory.getLogger(EnvironmentResource.class);
        
    @Inject
    private EnvironmentRepository environmentRepository;
    
    @Inject
    private EnvironmentSearchRepository environmentSearchRepository;
    
    /**
     * POST  /environments -> Create a new environment.
     */
    @RequestMapping(value = "/environments",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Environment> createEnvironment(@Valid @RequestBody Environment environment) throws URISyntaxException {
        log.debug("REST request to save Environment : {}", environment);
        if (environment.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("environment", "idexists", "A new environment cannot already have an ID")).body(null);
        }
        Environment result = environmentRepository.save(environment);
        environmentSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/environments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("environment", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /environments -> Updates an existing environment.
     */
    @RequestMapping(value = "/environments",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Environment> updateEnvironment(@Valid @RequestBody Environment environment) throws URISyntaxException {
        log.debug("REST request to update Environment : {}", environment);
        if (environment.getId() == null) {
            return createEnvironment(environment);
        }
        Environment result = environmentRepository.save(environment);
        environmentSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("environment", environment.getId().toString()))
            .body(result);
    }

    /**
     * GET  /environments -> get all the environments.
     */
    @RequestMapping(value = "/environments",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Environment>> getAllEnvironments(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Environments");
        Page<Environment> page = environmentRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/environments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /environments/:id -> get the "id" environment.
     */
    @RequestMapping(value = "/environments/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Environment> getEnvironment(@PathVariable Long id) {
        log.debug("REST request to get Environment : {}", id);
        Environment environment = environmentRepository.findOne(id);
        return Optional.ofNullable(environment)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /environments/:id -> delete the "id" environment.
     */
    @RequestMapping(value = "/environments/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEnvironment(@PathVariable Long id) {
        log.debug("REST request to delete Environment : {}", id);
        environmentRepository.delete(id);
        environmentSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("environment", id.toString())).build();
    }

    /**
     * SEARCH  /_search/environments/:query -> search for the environment corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/environments/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Environment> searchEnvironments(@PathVariable String query) {
        log.debug("REST request to search Environments for query {}", query);
        return StreamSupport
            .stream(environmentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
