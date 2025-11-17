package ticket.reserve.inventory.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ticket.reserve.inventory.application.port.out.EventPort;
import ticket.reserve.inventory.application.dto.response.EventDetailResponseDto;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient extends EventPort {

    @Override
    @GetMapping("/api/events/{eventId}")
    EventDetailResponseDto getOne(@PathVariable("eventId") Long eventId);
}
