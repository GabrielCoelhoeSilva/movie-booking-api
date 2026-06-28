package com.gabriel.moviebooking.factory;

import com.gabriel.moviebooking.entity.Seat;
import com.gabriel.moviebooking.entity.Session;

import java.util.ArrayList;
import java.util.List;

public class SeatGenerator {

    public static List<Seat> generateSeats(int capacity, int seatsPerRow, Session session) {

        List<Seat> seats = new ArrayList<>();

        /*
        int rowsNeeded = (int) Math.ceil((double) capacity / seatsPerRow);

        char rowLetter = 'A';
        int seatCount = 0;

        for (int i = 0; i < rowsNeeded; i++) {

            for (int j = 1; j <= seatsPerRow; j++) {

                if (seatCount >= capacity) {
                    break;
                }

                Seat seat = new Seat();
                seat.setRow(String.valueOf(rowLetter));
                seat.setNumber(j);
                seat.setAvailable(true);
                seat.setSession(session);

                seats.add(seat);

                seatCount++;
            }

            rowLetter++;
        }
        */

        return seats;
    }
}