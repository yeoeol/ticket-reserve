package ticket.reserve.busking.domain.eventimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.busking.domain.eventimage.EventImage;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
}
