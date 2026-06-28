package com.gabriel.moviebooking.repository;

import com.gabriel.moviebooking.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("""
            SELECT COUNT(s) > 0 FROM Session s
            WHERE s.room.id = :roomId
            AND s.startTime < :endTime
            AND s.endTime > :startTime
            """)
    boolean existsConflictingSession(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}