package ticket.reserve.busking.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.busking.application.BuskingQueryService;
import ticket.reserve.busking.application.SearchService;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuskingQueryServiceImpl implements BuskingQueryService {
    
    private final BuskingRepository buskingRepository;
    private final SearchService searchService;

    @Override
    @Transactional(readOnly = true)
    public Busking findById(Long id) {
        return buskingRepository.findByIdWithImage(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BUSKING_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getIds() {
        return buskingRepository.findIds();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuskingResponseDto> readAllWithCursor(Long lastBuskingId, int size) {
        return searchService.readAllWithCursor(lastBuskingId, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuskingResponseDto> getAll() {
        return buskingRepository.findAll().stream()
                .map(BuskingResponseDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuskingResponseDto> findAllByBulk(List<Long> buskingIds) {
        return buskingRepository.findAllByIdIn(buskingIds).stream()
                .map(BuskingResponseDto::from)
                .toList();
    }
}
