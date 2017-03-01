package ch.corner.envres.C_service;

import ch.corner.envres.Application;
import ch.corner.envres.B_web.rest.TestUtil;
import ch.corner.envres.domain.Appl;
import ch.corner.envres.domain.Environment;
import ch.corner.envres.domain.Reservation;
import ch.corner.envres.domain.User;
import ch.corner.envres.repository.ApplRepository;
import ch.corner.envres.repository.EnvironmentRepository;
import ch.corner.envres.repository.ReservationRepository;
import ch.corner.envres.repository.UserRepository;
import ch.corner.envres.service.ClashingReservationException;
import ch.corner.envres.service.ReservationService;
import ch.corner.envres.service.UserService;
import ch.corner.envres.web.rest.ReservationResource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class ReservationServiceIntTest {


	@Inject
	private ReservationRepository reservationRepository;

	@Inject
	private ApplRepository applRepository;

	@Inject
	private EnvironmentRepository environmentRepository;

	@Inject
	private UserRepository userRepository;



	@Inject
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Inject
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	private MockMvc restReservationMockMvc;

	@Inject
	private ReservationService reservationService;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		ReservationResource reservationResource = new ReservationResource();
		ReflectionTestUtils.setField(reservationResource, "reservationService", reservationService);
		this.restReservationMockMvc = MockMvcBuilders.standaloneSetup(reservationResource)
				.setCustomArgumentResolvers(pageableArgumentResolver)
				.setMessageConverters(jacksonMessageConverter).build();
	}


	private Reservation createReservation(Appl appl, Environment env, LocalDate startDate, LocalDate endDate, String project, User requestor){
		Reservation reservation = new Reservation();
		reservation.setAppl(appl);
		reservation.setEnvironment(env);
		reservation.setStartDate(startDate);
		reservation.setEndDate(endDate);
		reservation.setProject(project);
		reservation.setRequestor(requestor);

		return reservation;
	}

	@Test
	public void testClashingReservations() throws IOException, Exception{
		Appl appl = applRepository.findAll().get(0);
		Environment env = environmentRepository.findAll().get(0);
		User user = userRepository.findOneByLogin("admin").get();

		Reservation clashingReservation = createReservation(appl
				,env
				,LocalDate.now().minus(1,ChronoUnit.MONTHS)
				,LocalDate.now().plus(2,ChronoUnit.MONTHS)
				,"prj"
				,user);


		restReservationMockMvc.perform(post("/api/reservations")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(clashingReservation)));

		Reservation reservation = createReservation(appl
				,env
				,LocalDate.now()
				,LocalDate.now().plus(1,ChronoUnit.MONTHS)
				,"prj"
				,user);

		try {
			restReservationMockMvc.perform(post("/api/reservations")
					.contentType(TestUtil.APPLICATION_JSON_UTF8)
					.content(TestUtil.convertObjectToJsonBytes(reservation)));
		}catch(ClashingReservationException e){
			assertThat(e.getClashingReservations()).hasSize(1);
			assertThat(e.getClashingReservations().get(0)).isEqualTo(clashingReservation);
		}

	}

}
