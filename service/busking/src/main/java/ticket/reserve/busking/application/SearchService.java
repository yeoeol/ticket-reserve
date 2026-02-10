package ticket.reserve.busking.application;

import ticket.reserve.busking.application.dto.response.BuskingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchService {
    List<BuskingResponseDto> search(String title, String location, LocalDateTime startTime, LocalDateTime endTime);
}
