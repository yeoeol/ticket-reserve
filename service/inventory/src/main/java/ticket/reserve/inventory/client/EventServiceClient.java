package ticket.reserve.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ticket.reserve.inventory.client.dto.EventDetailResponseDto;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient {

    @GetMapping("/api/events/{eventId}")
    EventDetailResponseDto getOne(@PathVariable("eventId") Long eventId);
}
