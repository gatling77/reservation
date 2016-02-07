package ch.corner.envres.web.rest;

import com.codahale.metrics.annotation.Timed;
import ch.corner.envres.domain.Appl;
import ch.corner.envres.repository.ApplRepository;
import ch.corner.envres.repository.search.ApplSearchRepository;
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
 * REST controller for managing Appl.
 */
@RestController
@RequestMapping("/api")
public class ApplResource {

    private final Logger log = LoggerFactory.getLogger(ApplResource.class);
        
    @Inject
    private ApplRepository applRepository;
    
    @Inject
    private ApplSearchRepository applSearchRepository;
    
    /**
     * POST  /appls -> Create a new appl.
     */
    @RequestMapping(value = "/appls",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appl> createAppl(@Valid @RequestBody Appl appl) throws URISyntaxException {
        log.debug("REST request to save Appl : {}", appl);
        if (appl.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("appl", "idexists", "A new appl cannot already have an ID")).body(null);
        }
        Appl result = applRepository.save(appl);
        applSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/appls/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("appl", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /appls -> Updates an existing appl.
     */
    @RequestMapping(value = "/appls",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appl> updateAppl(@Valid @RequestBody Appl appl) throws URISyntaxException {
        log.debug("REST request to update Appl : {}", appl);
        if (appl.getId() == null) {
            return createAppl(appl);
        }
        Appl result = applRepository.save(appl);
        applSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("appl", appl.getId().toString()))
            .body(result);
    }

    /**
     * GET  /appls -> get all the appls.
     */
    @RequestMapping(value = "/appls",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Appl>> getAllAppls(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Appls");
        Page<Appl> page = applRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/appls");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /appls/:id -> get the "id" appl.
     */
    @RequestMapping(value = "/appls/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appl> getAppl(@PathVariable Long id) {
        log.debug("REST request to get Appl : {}", id);
        Appl appl = applRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(appl)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /appls/:id -> delete the "id" appl.
     */
    @RequestMapping(value = "/appls/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAppl(@PathVariable Long id) {
        log.debug("REST request to delete Appl : {}", id);
        applRepository.delete(id);
        applSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("appl", id.toString())).build();
    }

    /**
     * SEARCH  /_search/appls/:query -> search for the appl corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/appls/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Appl> searchAppls(@PathVariable String query) {
        log.debug("REST request to search Appls for query {}", query);
        return StreamSupport
            .stream(applSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
