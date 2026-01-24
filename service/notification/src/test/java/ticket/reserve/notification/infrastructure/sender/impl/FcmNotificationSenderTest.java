package ticket.reserve.notification.infrastructure.sender.impl;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.domain.notification.Notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmNotificationSenderTest {

    @InjectMocks
    FcmNotificationSender fcmNotificationSender;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private IdGenerator idGenerator;

    @Test
    @DisplayName("알림 제목과 내용 전달 성공 시 성공 결과를 반환한다")
    void send_notification_success() throws Exception {
        //given
        Notification notification = Notification.create(idGenerator, "제목", "내용", 1L, 1234L);
        String fcmToken = "testFcmToken";

        //when
        NotificationResult result = fcmNotificationSender.send(notification, fcmToken);

        //then
        verify(firebaseMessaging, times(1)).send(any(Message.class));
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.errorCode()).isNull();
    }

    @Test
    @DisplayName("알림 제목과 내용 전달 실패 시 실패 결과를 반환한다")
    void send_notification_fail() throws Exception {
        //given
        Notification notification = Notification.create(idGenerator, "제목", "내용", 1L, 1234L);
        String fcmToken = "testFcmToken";

        doThrow(FirebaseMessagingException.class).when(firebaseMessaging).send(any());

        //when
        NotificationResult result = fcmNotificationSender.send(notification, fcmToken);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.errorCode()).isNotNull();
    }
}