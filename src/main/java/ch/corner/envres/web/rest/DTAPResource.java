package ch.corner.envres.web.rest;

import com.codahale.metrics.annotation.Timed;
import ch.corner.envres.domain.DTAP;
import ch.corner.envres.repository.DTAPRepository;
import ch.corner.envres.repository.search.DTAPSearchRepository;
import ch.corner.envres.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing DTAP.
 */
@RestController
@RequestMapping("/api")
public class DTAPResource {

    private final Logger log = LoggerFactory.getLogger(DTAPResource.class);
        
    @Inject
    private DTAPRepository dTAPRepository;
    
    @Inject
    private DTAPSearchRepository dTAPSearchRepository;
    
    /**
     * POST  /dTAPs -> Create a new dTAP.
     */
    @RequestMapping(value = "/dTAPs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DTAP> createDTAP(@Valid @RequestBody DTAP dTAP) throws URISyntaxException {
        log.debug("REST request to save DTAP : {}", dTAP);
        if (dTAP.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dTAP", "idexists", "A new dTAP cannot already have an ID")).body(null);
        }
        DTAP result = dTAPRepository.save(dTAP);
        dTAPSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/dTAPs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("dTAP", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dTAPs -> Updates an existing dTAP.
     */
    @RequestMapping(value = "/dTAPs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DTAP> updateDTAP(@Valid @RequestBody DTAP dTAP) throws URISyntaxException {
        log.debug("REST request to update DTAP : {}", dTAP);
        if (dTAP.getId() == null) {
            return createDTAP(dTAP);
        }
        DTAP result = dTAPRepository.save(dTAP);
        dTAPSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("dTAP", dTAP.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dTAPs -> get all the dTAPs.
     */
    @RequestMapping(value = "/dTAPs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<DTAP> getAllDTAPs() {
        log.debug("REST request to get all DTAPs");
        return dTAPRepository.findAll();
            }

    /**
     * GET  /dTAPs/:id -> get the "id" dTAP.
     */
    @RequestMapping(value = "/dTAPs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DTAP> getDTAP(@PathVariable Long id) {
        log.debug("REST request to get DTAP : {}", id);
        DTAP dTAP = dTAPRepository.findOne(id);
        return Optional.ofNullable(dTAP)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /dTAPs/:id -> delete the "id" dTAP.
     */
    @RequestMapping(value = "/dTAPs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDTAP(@PathVariable Long id) {
        log.debug("REST request to delete DTAP : {}", id);
        dTAPRepository.delete(id);
        dTAPSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("dTAP", id.toString())).build();
    }

    /**
     * SEARCH  /_search/dTAPs/:query -> search for the dTAP corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/dTAPs/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<DTAP> searchDTAPs(@PathVariable String query) {
        log.debug("REST request to search DTAPs for query {}", query);
        return StreamSupport
            .stream(dTAPSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
