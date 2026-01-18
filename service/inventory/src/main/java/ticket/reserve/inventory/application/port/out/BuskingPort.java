package ticket.reserve.inventory.application.port.out;

import ticket.reserve.inventory.application.dto.response.BuskingResponseDto;

public interface BuskingPort {
    BuskingResponseDto getOne(Long id);
}
