package ticket.reserve.event.infrastructure.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.EventCreatedEventPayload;
import ticket.reserve.event.application.port.out.EventPublishPort;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCreatedProducer implements EventPublishPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void createEvent(EventCreatedEventPayload payload) {
        String eventCreatedMessage = null;
        try {
            eventCreatedMessage = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("[EventCreatedProducer.createEvent]", e);
            throw new CustomException(ErrorCode.EVENT_CREATED_ERROR);
        }

        kafkaTemplate.send(EventType.Topic.TICKET_RESERVE_EVENT, eventCreatedMessage);
        log.info("[EventCreatedProducer.createEvent] " +
                "Kafka 메시지 전송 완료 - eventId={}, message={}", payload.getEventId(), eventCreatedMessage);
    }
}
