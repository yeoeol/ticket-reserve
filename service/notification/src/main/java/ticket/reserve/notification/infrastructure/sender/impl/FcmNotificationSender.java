package ticket.reserve.notification.infrastructure.sender.impl;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
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
    public CompletableFuture<BatchResponse> send(String title, String body, Long buskingId, List<String> tokens) {
        MulticastMessage multicastMessage = createMessage(title, body, buskingId, tokens);

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);
            return CompletableFuture.completedFuture(response);
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
