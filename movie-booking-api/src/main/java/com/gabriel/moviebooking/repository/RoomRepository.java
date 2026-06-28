package com.gabriel.moviebooking.repository;

import com.gabriel.moviebooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
