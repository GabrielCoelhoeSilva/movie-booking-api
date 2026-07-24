package com.gabriel.moviebooking.entity;

import com.gabriel.moviebooking.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer seatsPerRow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Session> sessions;

    @Enumerated(EnumType.STRING)
    private RoomType type;
}
