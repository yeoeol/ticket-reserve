package ticket.reserve.busking.domain.busking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.busking.domain.busking.Busking;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BuskingRepository extends JpaRepository<Busking, Long> {
    @Query(value = "SELECT b.id FROM Busking b")
    List<Long> findIds();

    @Query(value =
            "SELECT b FROM Busking b " +
            "LEFT JOIN FETCH b.buskingImages bi " +
            "WHERE b.id = :id")
    Optional<Busking> findByIdWithImage(Long id);

    List<Busking> findAllByIdIn(Collection<Long> ids);
}
