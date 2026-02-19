package ticket.reserve.notification.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.notification.application.NotificationQueryService;
import ticket.reserve.notification.application.dto.response.NotificationHistoryResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationQueryService notificationQueryService;

    @GetMapping("/histories")
    public ResponseEntity<List<NotificationHistoryResponseDto>> getAll(@AuthenticationPrincipal String userId) {
        List<NotificationHistoryResponseDto> notificationHistoryResponseDtos = notificationQueryService
                .findHistoriesByUserId(Long.valueOf(userId)).stream()
                .map(NotificationHistoryResponseDto::from)
                .toList();

        return ResponseEntity.ok(notificationHistoryResponseDtos);
    }

}
