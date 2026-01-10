package ticket.reserve.image.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.image.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
