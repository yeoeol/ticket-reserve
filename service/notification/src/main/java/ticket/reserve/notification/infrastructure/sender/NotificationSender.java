package ticket.reserve.notification.infrastructure.sender;

import com.google.firebase.messaging.BatchResponse;
import ticket.reserve.notification.application.port.out.SenderPort;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationSender extends SenderPort {
    CompletableFuture<BatchResponse> send(String title, String body, Long buskingId, List<String> tokens);
}
