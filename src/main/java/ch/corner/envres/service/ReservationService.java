package ch.corner.envres.service;

import ch.corner.envres.domain.Reservation;
import ch.corner.envres.repository.ReservationRepository;
import ch.corner.envres.repository.search.ReservationSearchRepository;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
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


    public Reservation confirm(Reservation reservation) {
        reservation.confirm();
        return saveAndIndex(reservation);
    }

    public Reservation close(Reservation reservation) {
        reservation.close();
        return saveAndIndex(reservation);
    }

    /**
     * Save a reservation.
     *
     * @return the persisted entity
     */
    public Reservation save(Reservation reservation) throws ClashingReservationException {
        log.debug("Request to save Reservation : {}", reservation);

        if (reservation.isNew() || thereIsAChangeInDates(reservation)) {
            return checkForConflictsAndSave(reservation);
        } else { // Reservation is not new and the dates haven't changed
            return saveAndIndex(reservation);
        }

    }

    private Reservation saveAndIndex(Reservation reservation){
        reservation = reservationRepository.save(reservation);
        return reservationSearchRepository.save(reservation);
    }

    private Reservation checkForConflictsAndSave(Reservation reservation) throws ClashingReservationException {
        List<Reservation> clashingReservation = findClashingReservations(reservation);

        if (clashingReservation.isEmpty()) {
            reservation.markAsConfirmed();
            log.debug("Reservation confirmed");
            return saveAndIndex(reservation);
        } else {
            reservation.markAsConflicted();
            Reservation result = saveAndIndex(reservation);
            throw new ClashingReservationException(result, clashingReservation);
        }
    }

    private boolean thereIsAChangeInDates(Reservation newStatus) {
        Reservation previousStatus = reservationRepository.findOne(newStatus.getId());

        return !(previousStatus.getStartDate().equals(newStatus.getStartDate())
            && previousStatus.getEndDate().equals(newStatus.getEndDate()));
    }


    /**
     * get all the reservations.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Reservation> findAll(Pageable pageable) {
        log.debug("Request to get all Reservations");
        Page<Reservation> result = reservationRepository.findAll(pageable);
        return result;
    }

    /**
     * get one reservation by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Reservation findOne(Long id) {
        log.debug("Request to get Reservation : {}", id);
        Reservation reservation = reservationRepository.findOne(id);
        return reservation;
    }

    /**
     * delete the  reservation by id.
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
       QueryStringQueryBuilder queryBuilder = queryStringQuery(query)
           .field("reservation.environment.environmentDescription")
           .field("reservation.appl.applName")
           .field("reservation.project")
           .field("reservation.status")
           .field("reservation.requestor")
           ;

        return StreamSupport
            .stream(reservationSearchRepository.search(queryBuilder).spliterator(), false)
            .collect(Collectors.toList());
    }

    /**
     * search for the reservation corresponding
     * to the query.
     */
    @Transactional(readOnly = true)
    public List<Reservation> findClashingReservations(Reservation reservation) {

        log.debug("Search reservation with potential clash");

        return StreamSupport.
            stream(reservationRepository.findByApplAndEnvironment(
                reservation.getAppl().getId(),
                reservation.getEnvironment().getId()).spliterator(), false)
                .filter(r -> r.getStartDate().isBefore(reservation.getEndDate()) && r.getEndDate().isAfter(reservation.getStartDate()))
                .filter(r -> !r.isClosed())
            .collect(Collectors.toList());
    }
}
