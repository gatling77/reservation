package ch.corner.envres.service;

import ch.corner.envres.domain.Reservation;

import java.util.List;

/**
 * Created by gatling77 on 2/6/16.
 */
public class ClashingReservationException extends Exception {


    private final Reservation reservation;
    private final List<Reservation> clashingReservations;

    public ClashingReservationException(Reservation reservation, List<Reservation> clashingReservations){

        this.reservation = reservation;
        this.clashingReservations = clashingReservations;

    }

    public Reservation getReservation() {
        return reservation;
    }

    public List<Reservation> getClashingReservations() {
        return clashingReservations;
    }
}
