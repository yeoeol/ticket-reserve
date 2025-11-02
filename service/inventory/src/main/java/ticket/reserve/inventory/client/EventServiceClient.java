package ticket.reserve.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ticket.reserve.inventory.dto.EventResponseDto;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient {

    @GetMapping("/events/api/{eventId}")
    EventResponseDto getOne(@PathVariable("eventId") Long eventId);
}
