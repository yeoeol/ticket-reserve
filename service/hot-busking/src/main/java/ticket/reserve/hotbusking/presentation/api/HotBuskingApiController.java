package ticket.reserve.hotbusking.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket.reserve.hotbusking.application.HotBuskingService;
import ticket.reserve.hotbusking.application.dto.response.HotBuskingResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hot-buskings")
public class HotBuskingApiController {

    private final HotBuskingService hotBuskingService;

    @GetMapping("/buskings")
    public ResponseEntity<List<HotBuskingResponseDto>> readAll() {
        return ResponseEntity.ok(hotBuskingService.readAll());
    }
}
