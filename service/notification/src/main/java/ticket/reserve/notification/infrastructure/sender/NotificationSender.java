package ticket.reserve.notification.infrastructure.sender;

import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;
import ticket.reserve.notification.application.port.out.SenderPort;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationSender extends SenderPort {
    CompletableFuture<NotificationBatchResponseDto> send(String title, String body, Long buskingId, List<String> tokens);
}
