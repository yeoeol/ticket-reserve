package ticket.reserve.busking.domain.eventimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.busking.domain.eventimage.BuskingImage;

public interface BuskingImageRepository extends JpaRepository<BuskingImage, Long> {
}
