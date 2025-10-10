package ticket.reserve.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
