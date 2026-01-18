package ticket.reserve.busking.domain.buskingimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.busking.domain.buskingimage.BuskingImage;

public interface BuskingImageRepository extends JpaRepository<BuskingImage, Long> {
}
