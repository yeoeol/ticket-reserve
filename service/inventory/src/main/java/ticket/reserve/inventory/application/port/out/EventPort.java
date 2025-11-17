package ticket.reserve.inventory.application.port.out;

import ticket.reserve.inventory.application.dto.response.EventDetailResponseDto;

public interface EventPort {
    EventDetailResponseDto getOne(Long id);
}
