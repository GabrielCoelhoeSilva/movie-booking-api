package com.gabriel.moviebooking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String row;
    private int number;

    private boolean available = true;

    //@ManyToOne
    //@JoinColumn(name = "session_id")
    //private Session session;


}