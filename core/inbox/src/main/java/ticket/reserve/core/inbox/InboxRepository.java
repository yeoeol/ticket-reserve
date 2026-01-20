package ticket.reserve.core.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, Long> {
}
