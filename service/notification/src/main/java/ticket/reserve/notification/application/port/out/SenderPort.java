package ticket.reserve.notification.application.port.out;

import com.google.firebase.messaging.BatchResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SenderPort {
    CompletableFuture<BatchResponse> send(String title, String body, Long buskingId, List<String> tokens);
}
