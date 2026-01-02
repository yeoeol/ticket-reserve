package ticket.reserve.reservation.infrastucture.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.reservation.application.port.out.EventPort;

import java.util.List;

@Component
public class EventRestClientAdapter implements EventPort {

    private final RestClient restClient;

    public EventRestClientAdapter(
            @Value("${endpoints.ticket-reserve-event-service.url}") String eventServiceUrl
    ) {
        this.restClient = RestClient.create(eventServiceUrl);
    }

    @Override
    public List<Long> getEventIds() {
        return restClient.get()
                .uri("/api/events/event-ids")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Long>>() {});
    }
}
