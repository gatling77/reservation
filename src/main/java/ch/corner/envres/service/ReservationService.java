package ch.corner.envres.service;

import ch.corner.envres.domain.Reservation;
import ch.corner.envres.repository.ReservationRepository;
import ch.corner.envres.repository.search.ReservationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Reservation.
 */
@Service
@Transactional
public class ReservationService {

    private final Logger log = LoggerFactory.getLogger(ReservationService.class);

    @Inject
    private ReservationRepository reservationRepository;

    @Inject
    private ReservationSearchRepository reservationSearchRepository;

    /**
     * Save a reservation.
     * @return the persisted entity
     */
    public Reservation save(Reservation reservation) throws ClashingReservationException {
        log.debug("Request to save Reservation : {}", reservation);
        List<Reservation> clashingReservation = findClashingReservations(reservation);

        if (clashingReservation.isEmpty()) {
            reservation.confirm();
            log.debug("Reservation confirmed");
            Reservation result = reservationRepository.save(reservation);
            return result;
        }else{
            reservation.conflicted();
            Reservation result = reservationRepository.save(reservation);
            throw new ClashingReservationException(result,clashingReservation);
        }

    }

    /**
     *  get all the reservations.
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Reservation> findAll(Pageable pageable) {
        log.debug("Request to get all Reservations");
        Page<Reservation> result = reservationRepository.findAll(pageable);
        return result;
    }

    /**
     *  get one reservation by id.
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Reservation findOne(Long id) {
        log.debug("Request to get Reservation : {}", id);
        Reservation reservation = reservationRepository.findOne(id);
        return reservation;
    }

    /**
     *  delete the  reservation by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete Reservation : {}", id);
        reservationRepository.delete(id);
        reservationSearchRepository.delete(id);
    }

    /**
     * search for the reservation corresponding
     * to the query.
     */
    @Transactional(readOnly = true)
    public List<Reservation> search(String query) {

        log.debug("REST request to search Reservations for query {}", query);
        return StreamSupport
            .stream(reservationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    /**
     * search for the reservation corresponding
     * to the query.
     */
    @Transactional(readOnly = true)
    private List<Reservation> findClashingReservations(Reservation reservation) {

        log.debug("Search reservation with potential clash");

        return StreamSupport.
            stream(reservationRepository.findByApplAndEnvironment(
                reservation.getAppl().getId(),
                reservation.getEnvironment().getId()).spliterator(),false)
            .filter(r -> r.getStartDate().isBefore(reservation.getEndDate()) && r.getEndDate().isAfter(reservation.getStartDate()))
            .filter(r -> !r.isClosed())
            .collect(Collectors.toList());
    }
}
