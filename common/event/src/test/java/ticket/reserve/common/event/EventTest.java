package ticket.reserve.common.event;

import org.junit.jupiter.api.Test;
import ticket.reserve.common.event.payload.BuskingCreatedEventPayload;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void serde() {
        // given
        BuskingCreatedEventPayload payload = BuskingCreatedEventPayload.builder()
                .buskingId(1L)
                .title("titleTest")
                .description("descTest")
                .location("한국")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plus(Duration.ofDays(1)))
                .totalInventoryCount(10)
                .build();

        Event<EventPayload> event = Event.of(
                1234L,
                EventType.EVENT_CREATED,
                payload
        );
        String json = event.toJson();
        System.out.println("json = " + json);

        // when
        Event<EventPayload> result = Event.fromJson(json);

        // then
        assertThat(result.getEventId()).isEqualTo(event.getEventId());
        assertThat(result.getType()).isEqualTo(event.getType());
        assertThat(result.getPayload()).isInstanceOf(event.getPayload().getClass());

        BuskingCreatedEventPayload resultPayload = (BuskingCreatedEventPayload) result.getPayload();
        assertThat(resultPayload.getBuskingId()).isEqualTo(payload.getBuskingId());
        assertThat(resultPayload.getTitle()).isEqualTo(payload.getTitle());
        assertThat(resultPayload.getDescription()).isEqualTo(payload.getDescription());
        assertThat(resultPayload.getLocation()).isEqualTo(payload.getLocation());
        assertThat(resultPayload.getStartTime()).isEqualTo(payload.getStartTime());
        assertThat(resultPayload.getEndTime()).isEqualTo(payload.getEndTime());
        assertThat(resultPayload.getTotalInventoryCount()).isEqualTo(payload.getTotalInventoryCount());
    }
}