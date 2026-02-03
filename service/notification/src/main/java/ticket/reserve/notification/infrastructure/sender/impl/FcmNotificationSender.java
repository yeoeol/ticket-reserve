package ticket.reserve.notification.infrastructure.sender.impl;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;
import ticket.reserve.notification.infrastructure.sender.NotificationSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmNotificationSender implements NotificationSender {

    private final FirebaseMessaging firebaseMessaging;

    @Async("sendExecutor")
    @Override
    public CompletableFuture<NotificationBatchResponseDto> send(String title, String body, Long buskingId, List<String> tokens) {
        MulticastMessage multicastMessage = createMessage(title, body, buskingId, tokens);

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(multicastMessage);
            List<SendResponse> sendResponses = batchResponse.getResponses();

            List<NotificationBatchResponseDto.NotificationSendResponseDto> notificationSendResponses = sendResponses.stream()
                    .map(r -> NotificationBatchResponseDto.NotificationSendResponseDto.of(
                            r.getMessageId(),
                            r.getException().getErrorCode().name(),
                            r.getException().getMessage())
                    ).toList();

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
