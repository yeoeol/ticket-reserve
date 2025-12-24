package ticket.reserve.event.infrastructure.messaging;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ticket.reserve.common.outboxmessagerelay.AbstractMessageRelay;
import ticket.reserve.common.outboxmessagerelay.OutboxRepository;

import java.util.concurrent.TimeUnit;

@Component
public class EventMessageRelay extends AbstractMessageRelay {

    public EventMessageRelay(OutboxRepository outboxRepository, KafkaTemplate<String, String> messageRelayKafkaTemplate) {
        super(outboxRepository, messageRelayKafkaTemplate);
    }

    @Override
    @Scheduled(
            fixedDelay = 10,
            initialDelay = 5,
            timeUnit = TimeUnit.SECONDS
    )
    @SchedulerLock(
            name = "eventMessageRelayLock",
            lockAtMostFor = "9s",
            lockAtLeastFor = "5s"
    )
    public void runRelayProcess() {
        super.runRelayProcess();
    }
}
