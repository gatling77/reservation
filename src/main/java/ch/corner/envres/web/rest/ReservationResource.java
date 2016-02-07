package ch.corner.envres.web.rest;

import ch.corner.envres.domain.Reservation;
import ch.corner.envres.service.ClashingReservationException;
import ch.corner.envres.service.ReservationService;
import ch.corner.envres.web.rest.util.HeaderUtil;
import ch.corner.envres.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Reservation.
 */
@RestController
@RequestMapping("/api")
public class ReservationResource {

    private final Logger log = LoggerFactory.getLogger(ReservationResource.class);

    @Inject
    private ReservationService reservationService;



    /**
     * POST  /reservations -> Create a new reservation.
     */
    @RequestMapping(value = "/reservations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reservation> createReservation(@Validated(Reservation.InputValidation.class) @RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to save Reservation : {}", reservation);

        if (reservation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("reservation", "idexists", "A new reservation cannot already have an ID")).body(null);
        }

        Reservation result;
        try {
            result = reservationService.save(reservation);
            return ResponseEntity.created(new URI("/api/reservations/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("reservation", result.getId().toString()))
                .body(result);
        } catch (ClashingReservationException e) {
            result = e.getReservation();
            return ResponseEntity.created(new URI("/api/reservations/" + result.getId()))
                .headers(HeaderUtil.createWarning("Reservation conflicts with other reservations",""+e.getClashingReservations().size()))
                .body(result);
        }

    }

    /**
     * PUT  /reservations -> Updates an existing reservation.
     */
    @RequestMapping(value = "/reservations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reservation> updateReservation(@Valid @RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to update Reservation : {}", reservation);
        if (reservation.getId() == null) {
            return createReservation(reservation);
        }

        Reservation result;
        try {
            result = reservationService.save(reservation);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("reservation", reservation.getId().toString()))
                .body(result);
        }catch(ClashingReservationException e){
            result = e.getReservation();
            return ResponseEntity.ok()
                .headers(HeaderUtil.createWarning("Reservation conflicts with other reservations",""+e.getClashingReservations().size()))
                .body(result);
        }
    }

    /**
     * GET  /reservations -> get all the reservations.
     */
    @RequestMapping(value = "/reservations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Reservation>> getAllReservations(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Reservations");
        Page<Reservation> page = reservationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reservations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reservations/:id -> get the "id" reservation.
     */
    @RequestMapping(value = "/reservations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        log.debug("REST request to get Reservation : {}", id);
        Reservation reservation = reservationService.findOne(id);
        return Optional.ofNullable(reservation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reservations/:id -> delete the "id" reservation.
     */
    @RequestMapping(value = "/reservations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.debug("REST request to delete Reservation : {}", id);
        reservationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("reservation", id.toString())).build();
    }

    /**
     * SEARCH  /_search/reservations/:query -> search for the reservation corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/reservations/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Reservation> searchReservations(@PathVariable String query) {
        log.debug("Request to search Reservations for query {}", query);
        return reservationService.search(query);
    }
}
