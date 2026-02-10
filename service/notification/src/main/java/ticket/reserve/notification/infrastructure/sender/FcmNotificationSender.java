package ticket.reserve.notification.infrastructure.sender;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;
import ticket.reserve.notification.application.port.out.SenderPort;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmNotificationSender implements SenderPort {

    private final FirebaseMessaging firebaseMessaging;

    @Async("sendExecutor")
    @Override
    public CompletableFuture<NotificationBatchResponseDto> send(String title, String body, Long buskingId, List<String> tokens) {
        MulticastMessage multicastMessage = createMessage(title, body, buskingId, tokens);

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(multicastMessage);
            List<SendResponse> sendResponses = batchResponse.getResponses();

            List<NotificationBatchResponseDto.NotificationSendResponseDto> notificationSendResponses = sendResponses.stream()
                    .map(r -> {
                        if (r.isSuccessful()) {
                            return NotificationBatchResponseDto.NotificationSendResponseDto.of(
                                    r.getMessageId(), null, null
                            );
                        } else {
                            FirebaseMessagingException ex = r.getException();
                            return NotificationBatchResponseDto.NotificationSendResponseDto.of(
                                    r.getMessageId(),
                                    ex != null ? r.getException().getErrorCode().name() : null,
                                    ex != null ? r.getException().getMessage() : null);
                        }
                    }).toList();

            NotificationBatchResponseDto notificationBatchResponse = NotificationBatchResponseDto.of(
                    notificationSendResponses, batchResponse.getSuccessCount(), batchResponse.getFailureCount()
            );
            return CompletableFuture.completedFuture(notificationBatchResponse);
        } catch (FirebaseMessagingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private MulticastMessage createMessage(String title, String body, Long buskingId, List<String> tokens) {
        return MulticastMessage.builder()
                .addAllTokens(tokens)
                .putData("title", title)
                .putData("body", body)
                .putData("buskingId", String.valueOf(buskingId))
                .build();
    }
}
