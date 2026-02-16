package ticket.reserve.busking.application;

import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.infrastructure.persistence.querydsl.BuskingSearchCondition;

import java.util.List;

public interface SearchService {
    /**
     * 검색 조건 별 버스킹 조회
     */
    List<BuskingResponseDto> search(BuskingSearchCondition condition);

    /**
     * 커서 기반 무한 스크롤 버스킹 조회
     */
    List<BuskingResponseDto> readAllWithCursor(Long lastBuskingId, int size);
}
