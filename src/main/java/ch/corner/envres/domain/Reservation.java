package ch.corner.envres.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Reservation.
 */
@Entity
@Table(name = "reservation")
@Document(indexName = "reservation")
public class Reservation implements Serializable {

    private static class TransitionManager{
        private Set<String> allowedTransitions = new TreeSet<>();
        TransitionManager(){
            allowedTransitions.add(combine(STATUS_CONFLICT,STATUS_CONFIRMED));
            allowedTransitions.add(combine(STATUS_CONFIRMED,STATUS_CLOSED));
            allowedTransitions.add(combine(STATUS_CONFLICT,STATUS_CLOSED));
            allowedTransitions.add(combine(STATUS_NEW,STATUS_CONFLICT));
            allowedTransitions.add(combine(STATUS_NEW,STATUS_CONFIRMED));
        }
        private String combine(String status1,String status2){
            return status1+"/"+status2;
        }

        private boolean transitionAllowed(String status1,String status2){
            return allowedTransitions.contains(combine(status1,status2));
        }
        public void set(Reservation reservation, String newStatus) throws IllegalStateException{
            if(transitionAllowed(reservation.getStatus(),newStatus)){
                reservation.setStatus(newStatus);
            }else{
                throw new IllegalStateException("Status "+newStatus+" cannot be set");
            }
        }
    }

    private static final TransitionManager TRANSITION_MANAGER = new TransitionManager();
    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_CONFLICT = "CONFLICT";
    public static final String STATUS_CONFIRMED = "CONFIRMED";

    public interface InputValidation{}


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(groups = InputValidation.class)
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(groups = InputValidation.class)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(groups = InputValidation.class)
    @Size(min = 3, max = 50)
    @Column(name = "project", length = 50, nullable = false)
    private String project;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status = STATUS_NEW;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @ManyToOne
    @JoinColumn(name = "appl_id")
    private Appl appl;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getRequestor() {
        return requestor;
    }

    public void setRequestor(User user) {
        this.requestor = user;
    }

    public Appl getAppl() {
        return appl;
    }

    public void setAppl(Appl appl) {
        this.appl = appl;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reservation reservation = (Reservation) o;
        if(reservation.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, reservation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + id +
            ", startDate='" + startDate + "'" +
            ", endDate='" + endDate + "'" +
            ", project='" + project + "'" +
            ", status='" + status + "'" +
            '}';
    }

    public void confirm(){
        TRANSITION_MANAGER.set(this,STATUS_CONFIRMED);
    }

    public void conflicted(){
        TRANSITION_MANAGER.set(this,STATUS_CONFLICT);
    }

    public void close(){
        TRANSITION_MANAGER.set(this,STATUS_CLOSED);
    }

    public boolean isClosed(){
        return this.status.equals(STATUS_CLOSED);
    }
}
