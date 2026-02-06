package ticket.reserve.subscription.application.port.out;

import ticket.reserve.subscription.application.dto.response.BuskingResponseDto;

import java.util.List;

public interface BuskingPort {
    List<BuskingResponseDto> getAllByBuskingIds(List<Long> buskingIds);
}
