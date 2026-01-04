package ticket.reserve.event.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.event.application.dto.request.EventRequestDto;
import ticket.reserve.event.application.dto.response.EventDetailResponseDto;
import ticket.reserve.event.application.port.out.InventoryPort;
import ticket.reserve.event.domain.Event;
import ticket.reserve.event.domain.repository.EventRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    EventService eventService;

    @Mock EventRepository eventRepository;
    @Mock InventoryPort inventoryPort;
    @Mock OutboxEventPublisher outboxEventPublisher;

    private Event event;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .id(1234L)
                .eventTitle("testTitle")
                .description("testDesc")
                .location("장소")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .totalSeats(10)
                .build();
    }

    @Test
    @DisplayName("이벤트 생성 성공 - 요청 정보를 기반으로 이벤트 엔티티를 생성한다")
    void createEventSuccess() {
        //given
        EventRequestDto request = new EventRequestDto(
                1234L, "testTitle", "testDesc", "장소",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10
        );
        given(eventRepository.save(any()))
                .willReturn(event);

        //when
        EventDetailResponseDto response = eventService.createEvent(request);

        //then
        assertThat(response.id()).isEqualTo(event.getId());
        assertThat(response.eventTitle()).isEqualTo(event.getEventTitle());
        assertThat(response.location()).isEqualTo(event.getLocation());
    }

}