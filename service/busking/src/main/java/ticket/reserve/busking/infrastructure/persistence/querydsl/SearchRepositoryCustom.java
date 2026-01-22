package ticket.reserve.busking.infrastructure.persistence.querydsl;

import ticket.reserve.busking.application.dto.response.BuskingResponseDto;

import java.util.List;

public interface SearchRepositoryCustom {
    List<BuskingResponseDto> search(BuskingSearchCondition condition);
}
