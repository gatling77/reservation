package ch.corner.envres.service;

import ch.corner.envres.domain.Reservation;
import ch.corner.envres.repository.ReservationRepository;
import ch.corner.envres.repository.search.ReservationSearchRepository;

import org.apache.lucene.queryparser.surround.query.AndQuery;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

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
			for(Reservation r : clashingReservation) {
				if(r.getProject().equals(reservation.getProject())) { //same reservation, discard it
					reservation.setId((long)-1);
					reservation.setStatus("CONFLICT");
					throw new ClashingReservationException(reservation, new ArrayList<Reservation>());
				}
			}
			reservation.markAsConflicted();
			Reservation result = saveAndIndex(reservation);
			log.debug(result.toString());
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
	public List<Reservation> search( String reservationRequestor, String status, String project, String appName, String envDescription, String dateFrom, String dateTo) {

		log.debug("REST request to search Reservations for query reservationRequestor = {}; status = {}; project = {}; appName = {}, envDescription = {}; dateFrom = {}; dateTo = {}", 
				reservationRequestor, status, project, appName, envDescription, dateFrom, dateTo);

		if(dateFrom == null || dateFrom.equals("")) {
			dateFrom = "1900-01-01";
		}
		if(dateTo == null || dateTo.equals("")){
			dateTo = "2100-01-01";
		}

		return StreamSupport
				.stream(reservationRepository.findByTimeFrame(LocalDate.parse(dateFrom), LocalDate.parse(dateTo)).spliterator(), false)
				.filter(r -> reservationRequestor == null || reservationRequestor.equals("") || 
				r.getRequestor().getLogin().equals(reservationRequestor))
				.filter(r -> status == null || status.equals("") ||
				r.getStatus().equals(status))
				.filter(r -> project == null || project.equals("") ||
				r.getProject().equals(project))
				.filter(r -> appName == null || appName.equals("") ||
				r.getAppl().getApplName().equals(appName))
				.filter(r -> envDescription == null || envDescription.equals("") ||
				r.getEnvironment().getEnvironmentDescription().equals(envDescription))				
				.collect(Collectors.toList());
	}

	/**
	 * search for the reservation corresponding
	 * to the query.
	 */
	@Transactional(readOnly = true)
	public List<Reservation> findClashingReservations(Reservation reservation) {

		log.debug("Search reservation with potential clash");

		return StreamSupport
				.stream(reservationRepository.findByTimeFrame(reservation.getStartDate(), reservation.getEndDate()).spliterator(), false)
				.filter(r -> r.getAppl().getId() == reservation.getAppl().getId())
				.filter(r -> r.getEnvironment().getId() == reservation.getEnvironment().getId())
				.filter(r -> !r.isClosed())
				.collect(Collectors.toList());
	}
}
