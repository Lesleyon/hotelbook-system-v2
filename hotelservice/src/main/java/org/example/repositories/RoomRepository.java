package org.example.repositories;

import org.example.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAvailableTrueOrderByTimesBookedAscIdAsc();
    List<Room> findByAvailableTrue();
}