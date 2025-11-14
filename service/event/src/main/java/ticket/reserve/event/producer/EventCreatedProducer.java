package ticket.reserve.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.EventCreatedEventPayload;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCreatedProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void createEvent(EventCreatedEventPayload payload) {
        String eventCreatedMessage = null;
        try {
            eventCreatedMessage = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[EventCreatedProducer.createEvent] JSON 파싱 과정 중 오류 발생");
        }

        kafkaTemplate.send(EventType.Topic.TICKET_RESERVE_EVENT, eventCreatedMessage);
        log.info("[EventCreatedProducer.createEvent] " +
                "Kafka 메시지 전송 완료 - eventId={}, message={}", payload.getEventId(), eventCreatedMessage);
    }
}
