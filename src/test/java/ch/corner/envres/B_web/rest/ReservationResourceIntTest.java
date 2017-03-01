package ch.corner.envres.B_web.rest;

import ch.corner.envres.Application;
import ch.corner.envres.domain.Reservation;
import ch.corner.envres.repository.ReservationRepository;
import ch.corner.envres.service.ReservationService;
import ch.corner.envres.web.rest.ReservationResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ReservationResource REST controller.
 *
 * @see ReservationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ReservationResourceIntTest {


	private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
	private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

	private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
	private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
	private static final String DEFAULT_PROJECT = "AAA";
	private static final String UPDATED_PROJECT = "BBB";
	private static final String DEFAULT_STATUS = "AAAAA" ;
	private static final String UPDATED_STATUS = "BBBBB";

	@Inject
	private ReservationRepository reservationRepository;

	@Inject
	private ReservationService reservationService;

	@Inject
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Inject
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	private MockMvc restReservationMockMvc;

	private Reservation reservation;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		ReservationResource reservationResource = new ReservationResource();
		ReflectionTestUtils.setField(reservationResource, "reservationService", reservationService);
		this.restReservationMockMvc = MockMvcBuilders.standaloneSetup(reservationResource)
				.setCustomArgumentResolvers(pageableArgumentResolver)
				.setMessageConverters(jacksonMessageConverter).build();
	}

	@Before
	public void initTest() {
		reservation = new Reservation();
		reservation.setStartDate(DEFAULT_START_DATE);
		reservation.setEndDate(DEFAULT_END_DATE);
		reservation.setProject(DEFAULT_PROJECT);
		reservation.setStatus(DEFAULT_STATUS);
	}

	@Test
	@Transactional
	public void createReservation() throws Exception {

		int databaseSizeBeforeCreate = reservationRepository.findAll().size();

		// Create the Reservation
		reservation.setStatus(Reservation.STATUS_NEW);
		try{
			restReservationMockMvc.perform(post("/api/reservations")
					.contentType(TestUtil.APPLICATION_JSON_UTF8)
					.content(TestUtil.convertObjectToJsonBytes(reservation)))
			.andExpect(status().isCreated());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Validate the Reservation in the database
		List<Reservation> reservations = reservationRepository.findAll();
		assertThat(reservations).hasSize(databaseSizeBeforeCreate + 1);
		Reservation testReservation = reservations.get(reservations.size() - 1);
		assertThat(testReservation.getStartDate()).isEqualTo(DEFAULT_START_DATE);
		assertThat(testReservation.getEndDate()).isEqualTo(DEFAULT_END_DATE);
		assertThat(testReservation.getProject()).isEqualTo(DEFAULT_PROJECT);
		assertThat(testReservation.getStatus()).isEqualTo(Reservation.STATUS_CONFIRMED);
	}

	@Test
	@Transactional
	public void checkStartDateIsRequired() throws Exception {
		int databaseSizeBeforeTest = reservationRepository.findAll().size();
		// set the field null
		reservation.setStartDate(null);

		// Create the Reservation, which fails.

		restReservationMockMvc.perform(post("/api/reservations")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(reservation)))
		.andExpect(status().isBadRequest());

		List<Reservation> reservations = reservationRepository.findAll();
		assertThat(reservations).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void checkEndDateIsRequired() throws Exception {
		int databaseSizeBeforeTest = reservationRepository.findAll().size();
		// set the field null
		reservation.setEndDate(null);

		// Create the Reservation, which fails.

		restReservationMockMvc.perform(post("/api/reservations")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(reservation)))
		.andExpect(status().isBadRequest());

		List<Reservation> reservations = reservationRepository.findAll();
		assertThat(reservations).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void checkProjectIsRequired() throws Exception {
		int databaseSizeBeforeTest = reservationRepository.findAll().size();
		// set the field null
		reservation.setProject(null);

		// Create the Reservation, which fails.

		restReservationMockMvc.perform(post("/api/reservations")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(reservation)))
		.andExpect(status().isBadRequest());

		List<Reservation> reservations = reservationRepository.findAll();
		assertThat(reservations).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void checkInvalidStatus() throws Exception {
		int databaseSizeBeforeTest = reservationRepository.findAll().size();

		reservation.setStatus(null);
		// Create the Reservation, which fails.
		restReservationMockMvc.perform(post("/api/reservations")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(reservation)))
		.andExpect(status().isBadRequest());
		
		List<Reservation> reservations = reservationRepository.findAll();
		assertThat(reservations).hasSize(databaseSizeBeforeTest);
	}

	@Test
	@Transactional
	public void getAllReservations() throws Exception {
		// Initialize the database
		reservationRepository.saveAndFlush(reservation);

		// Get all the reservations
		restReservationMockMvc.perform(get("/api/reservations?sort=id,desc"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.[*].id").value(hasItem(reservation.getId().intValue())))
		.andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
		.andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
		.andExpect(jsonPath("$.[*].project").value(hasItem(DEFAULT_PROJECT.toString())))
		.andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
	}

	@Test
	@Transactional
	public void getReservation() throws Exception {
		// Initialize the database
		reservationRepository.saveAndFlush(reservation);

		// Get the reservation
		restReservationMockMvc.perform(get("/api/reservations/{id}", reservation.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id").value(reservation.getId().intValue()))
		.andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
		.andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
		.andExpect(jsonPath("$.project").value(DEFAULT_PROJECT.toString()))
		.andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingReservation() throws Exception {
		// Get the reservation
		restReservationMockMvc.perform(get("/api/reservations/{id}", Long.MAX_VALUE))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateReservation() throws Exception {
		// Initialize the database
		reservationRepository.saveAndFlush(reservation);

		int databaseSizeBeforeUpdate = reservationRepository.findAll().size();

		// Update the reservation
		reservation.setStartDate(UPDATED_START_DATE);
		reservation.setEndDate(UPDATED_END_DATE);
		reservation.setProject(UPDATED_PROJECT);
		reservation.setStatus(UPDATED_STATUS);

		restReservationMockMvc.perform(put("/api/reservations")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(reservation)))
		.andExpect(status().isOk());

		// Validate the Reservation in the database
		List<Reservation> reservations = reservationRepository.findAll();
		assertThat(reservations).hasSize(databaseSizeBeforeUpdate);
		Reservation testReservation = reservations.get(reservations.size() - 1);
		assertThat(testReservation.getStartDate()).isEqualTo(UPDATED_START_DATE);
		assertThat(testReservation.getEndDate()).isEqualTo(UPDATED_END_DATE);
		assertThat(testReservation.getProject()).isEqualTo(UPDATED_PROJECT);
		assertThat(testReservation.getStatus()).isEqualTo(UPDATED_STATUS);
	}

	@Test
	@Transactional
	public void deleteReservation() throws Exception {
		// Initialize the database
		reservationRepository.saveAndFlush(reservation);

		int databaseSizeBeforeDelete = reservationRepository.findAll().size();

		// Get the reservation
		restReservationMockMvc.perform(delete("/api/reservations/{id}", reservation.getId())
				.accept(TestUtil.APPLICATION_JSON_UTF8))
		.andExpect(status().isOk());

		// Validate the database is empty
		List<Reservation> reservations = reservationRepository.findAll();
		assertThat(reservations).hasSize(databaseSizeBeforeDelete - 1);
	}
}
