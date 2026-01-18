package ticket.reserve.reservation.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.event.EventType;
import ticket.reserve.reservation.application.ReservationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationEventConsumer {

    private final ReservationService reservationService;

    @KafkaListener(topics = {
            EventType.Topic.TICKET_RESERVE_PAYMENT,
            EventType.Topic.TICKET_RESERVE_RESERVATION
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[ReservationEventConsumer.listen] 이벤트 수신: message = {}", message);

        Event<EventPayload> event = Event.fromJson(message);
        if (event != null) {
            reservationService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
