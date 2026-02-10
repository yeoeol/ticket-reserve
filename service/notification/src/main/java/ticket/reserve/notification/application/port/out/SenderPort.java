package ticket.reserve.notification.application.port.out;

import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SenderPort {
    /**
     * 알림 제목, 내용, 대상을 받아서 알림을 비동기 전송한다.
     */
    CompletableFuture<NotificationBatchResponseDto> send(String title, String body, Long buskingId, List<String> tokens);
}
