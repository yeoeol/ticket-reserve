package ticket.reserve.busking.application;

import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.domain.busking.Busking;

import java.util.List;

public interface BuskingQueryService {
    Busking findById(Long id);

    List<BuskingResponseDto> getAll();

    List<Long> getIds();

    List<BuskingResponseDto> readAllWithCursor(Long lastBuskingId, int size);

    List<BuskingResponseDto> findAllByBulk(List<Long> buskingIds);
}
