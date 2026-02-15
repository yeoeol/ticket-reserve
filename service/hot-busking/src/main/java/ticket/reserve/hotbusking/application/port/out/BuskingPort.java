package ticket.reserve.hotbusking.application.port.out;

import ticket.reserve.hotbusking.application.dto.response.BuskingResponseDto;

public interface BuskingPort {
    BuskingResponseDto get(Long buskingId);
}
