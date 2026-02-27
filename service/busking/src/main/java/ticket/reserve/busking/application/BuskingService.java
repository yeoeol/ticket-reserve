package ticket.reserve.busking.application;

import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;

public interface BuskingService {
    BuskingResponseDto create(BuskingRequestDto request, MultipartFile file, Long userId);

    void delete(Long id);

    BuskingResponseDto getOne(Long buskingId, Long userId);

    void update(Long id, BuskingUpdateRequestDto request);

}
