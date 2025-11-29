package ticket.reserve.reservation.infrastucture.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient {

    @GetMapping("/api/events/event-ids")
    List<Long> getEventIds();
}
