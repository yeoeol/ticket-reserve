package ticket.reserve.event.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.event.domain.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
//    @Query(value = "SELECT e.id FROM Event e")
    @Query(value = "SELECT event_id FROM events", nativeQuery = true)
    List<Long> findEventIds();
}
