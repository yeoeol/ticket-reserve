package ticket.reserve.notification.application.port.out;

import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SenderPort {
    CompletableFuture<NotificationBatchResponseDto> send(String title, String body, Long buskingId, List<String> tokens);
}
