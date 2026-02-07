package ticket.reserve.busking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.infrastructure.persistence.querydsl.BuskingSearchCondition;
import ticket.reserve.busking.infrastructure.persistence.querydsl.SearchRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepositoryCustom searchRepository;

    @Transactional(readOnly = true)
    public List<BuskingResponseDto> search(String title, String location, LocalDateTime startTime, LocalDateTime endTime, Long userId) {
        BuskingSearchCondition condition = new BuskingSearchCondition(title, location, startTime, endTime);
        return searchRepository.search(condition);
    }
}
