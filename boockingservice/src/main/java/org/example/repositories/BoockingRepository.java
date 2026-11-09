package org.example.repositories;

import org.example.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BoockingRepository extends JpaRepository<Booking, Long> {
    Booking findByRequestId(String requestId);
    List<Booking> findByUserId(Long userId);
    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId " + "AND b.status IN ('PENDING','CONFIRMED') "
            + "AND b.startDate < :endDate AND b.endDate > :startDate")
    List<Booking> findConflicting(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
}