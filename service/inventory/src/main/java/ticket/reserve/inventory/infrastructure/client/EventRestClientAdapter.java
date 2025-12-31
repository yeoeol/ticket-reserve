package ticket.reserve.inventory.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.application.dto.response.EventDetailResponseDto;
import ticket.reserve.inventory.application.port.out.EventPort;

@Component
public class EventRestClientAdapter implements EventPort {

    private final RestClient restClient;

    public EventRestClientAdapter(
            @Value("${endpoints.ticket-reserve-event-service.url}") String eventServiceUrl
    ) {
        this.restClient = RestClient.create(eventServiceUrl);
    }

    @Override
    public EventDetailResponseDto getOne(Long eventId) {
        if (eventId == null) {
            throw new CustomException(ErrorCode.EVENT_NOT_FOUND);
        }

        return restClient.get()
                .uri("/api/events/{eventId}", eventId)
                .retrieve()
                .body(EventDetailResponseDto.class);
    }
}
