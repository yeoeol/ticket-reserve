package ticket.reserve.admin.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.admin.application.dto.event.request.EventRequestDto;
import ticket.reserve.admin.application.dto.event.request.EventUpdateRequestDto;
import ticket.reserve.admin.application.dto.event.response.EventDetailResponseDto;
import ticket.reserve.admin.application.port.out.EventPort;

import java.util.List;

@Component
public class EventRestClientAdapter implements EventPort {

    private final RestClient restClient;

    public EventRestClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${endpoints.ticket-reserve-event-service.url}") String eventServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(eventServiceUrl)
                .build();
    }

    @Override
    public List<EventDetailResponseDto> getEvents() {
        return restClient.get()
                .uri("/api/events")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public EventDetailResponseDto getEvent(Long eventId) {
        return restClient.get()
                .uri("/api/events/{eventId}", eventId)
                .retrieve()
                .body(EventDetailResponseDto.class);
    }

    @Override
    public EventDetailResponseDto createEvent(EventRequestDto request) {
        return restClient.post()
                .uri("/api/events")
                .body(request)
                .retrieve()
                .body(EventDetailResponseDto.class);
    }

    @Override
    public void updateEvent(Long eventId, EventUpdateRequestDto request) {
        restClient.put()
                .uri("/api/events/{eventId}", eventId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteEvent(Long eventId) {
        restClient.delete()
                .uri("/api/events/{eventId}", eventId)
                .retrieve()
                .toBodilessEntity();
    }
}
