package ch.corner.envres.service;

import ch.corner.envres.Application;
import ch.corner.envres.domain.Appl;
import ch.corner.envres.domain.Environment;
import ch.corner.envres.domain.Reservation;
import ch.corner.envres.domain.User;
import ch.corner.envres.repository.ApplRepository;
import ch.corner.envres.repository.EnvironmentRepository;
import ch.corner.envres.repository.ReservationRepository;
import ch.corner.envres.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

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
    private ReservationService reservationService;

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
    public void testClashingReservations() throws ClashingReservationException{
        Appl appl = applRepository.findAll().get(0);
        Environment env = environmentRepository.findAll().get(0);
        User user = userRepository.findOneByLogin("admin").get();

        Reservation clashingReservation = createReservation(appl
            ,env
            ,LocalDate.now().minus(1,ChronoUnit.MONTHS)
            ,LocalDate.now().plus(2,ChronoUnit.MONTHS)
            ,"prj"
            ,user);

        reservationService.save(clashingReservation);

        Reservation reservation = createReservation(appl
            ,env
            ,LocalDate.now()
            ,LocalDate.now().plus(1,ChronoUnit.MONTHS)
            ,"prj"
            ,user);

        try {
            reservationService.save(reservation);
        }catch(ClashingReservationException e){
            assertThat(e.getClashingReservations()).hasSize(1);
            assertThat(e.getClashingReservations().get(0)).isEqualTo(clashingReservation);
        }

    }

}
