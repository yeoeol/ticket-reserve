package ticket.reserve.core.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, Long> {
    Optional<Inbox> findByEventId(Long eventId);
}
