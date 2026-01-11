package ticket.reserve.event.domain.eventimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.event.domain.eventimage.EventImage;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
}
