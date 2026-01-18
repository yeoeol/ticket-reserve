package ticket.reserve.busking.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.busking.domain.event.Busking;

import java.util.List;

public interface BuskingRepository extends JpaRepository<Busking, Long> {
    @Query(value = "SELECT b.id FROM Busking b")
    List<Long> findIds();
}
