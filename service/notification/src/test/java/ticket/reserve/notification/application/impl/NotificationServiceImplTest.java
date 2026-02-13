package ticket.reserve.notification.application.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.FcmTokenService;
import ticket.reserve.notification.application.NotificationQueryService;
import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto;
import ticket.reserve.notification.application.dto.response.NotificationBatchResponseDto.NotificationSendResponseDto;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;
import ticket.reserve.notification.domain.notification.enums.NotificationStatus;
import ticket.reserve.notification.domain.notification.repository.BulkNotificationRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private SenderPort senderPort;
    @Mock
    private FcmTokenService fcmTokenService;
    @Mock
    private BulkNotificationRepository bulkNotificationRepository;
    @Mock
    private NotificationQueryService notificationQueryService;

    @Test
    @DisplayName("500명 이상의 대량 알림 발송 시 파티셔닝 및 결과가 정상 처리된다")
    void send_bulk_notification_success() {
        //given
        List<Long> userIds = LongStream.range(1, 601)
                .boxed()
                .toList();
        String title = "버스킹 알림 제목";
        String body = "버스킹 알림 내용";
        Long buskingId = 100L;

        Map<Long, String> tokenMap = userIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> "token_" + id
                ));

        given(fcmTokenService.getTokenMapByUserIds(anyList()))
                .willAnswer(invocation -> {
                    List<Long> ids = invocation.getArgument(0);
                    return ids.stream().collect(
                            Collectors.toMap(
                                    id -> id,
                                    tokenMap::get
                            )
                    );
                });

        given(senderPort.send(anyString(), anyString(), anyLong(), anyList()))
                .willAnswer(invocation -> {
                    List<String> tokens = invocation.getArgument(3);
                    List<NotificationSendResponseDto> sendResponses = tokens.stream()
                            .map(t -> new NotificationSendResponseDto(
                                    UUID.randomUUID().toString().substring(0, 7), null, null)
                            ).toList();
                    return CompletableFuture.completedFuture(NotificationBatchResponseDto.of(
                            sendResponses, 0, 0
                    ));
                });

        //when
        notificationService.sendBulkNotification(
                title, body, buskingId, userIds
        );

        //then
        verify(senderPort, times(2))
                .send(eq(title), eq(body), eq(buskingId), anyList());

        verify(bulkNotificationRepository, times(1)).bulkInsert(anyList());
        verify(bulkNotificationRepository, times(2)).bulkUpsert(anyList());
    }

    @Test
    @DisplayName("토큰이 없는 사용자는 즉시 실패 처리된다")
    void send_bulk_notification_fail_when_tokenNotFound() {
        //given
        String title = "버스킹 알림 제목";
        String body = "버스킹 알림 내용";
        Long buskingId = 100L;

        List<Long> userIds = List.of(1L);
        given(fcmTokenService.getTokenMapByUserIds(userIds)).willReturn(Map.of());

        //when
        notificationService.sendBulkNotification(title, body, buskingId, userIds);

        //then
        verify(senderPort, never()).send(any(), any(), any(), any());
        verify(bulkNotificationRepository).bulkUpsert(anyList());
    }

    @Test
    @DisplayName("성공 응답을 받으면 엔티티의 상태가 SUCCESS로 업데이트된다")
    void verify_notification_status_success() {
        //given
        List<Long> userIds = List.of(1L, 2L);
        String title = "버스킹 알림 제목";
        String body = "버스킹 알림 내용";
        Long buksingId = 100L;

        given(fcmTokenService.getTokenMapByUserIds(anyList()))
                .willReturn(Map.of(1L, "token1", 2L, "token2"));

        NotificationBatchResponseDto successResponse = NotificationBatchResponseDto.of(
                List.of(
                        NotificationSendResponseDto.of("msg1", null, null),
                        NotificationSendResponseDto.of("msg2", null, null)
                ),
                0,
                0
        );

        given(senderPort.send(anyString(), anyString(), anyLong(), anyList()))
                .willReturn(CompletableFuture.completedFuture(successResponse));

        //when
        notificationService.sendBulkNotification(title, body, buksingId, userIds);

        //then
        ArgumentCaptor<List<Notification>> successCaptor = ArgumentCaptor.forClass(List.class);

        verify(bulkNotificationRepository).bulkInsert(anyList());
        verify(bulkNotificationRepository).bulkUpsert(successCaptor.capture());

        List<Notification> successCaptured = successCaptor.getValue();

        assertThat(successCaptured).hasSize(2);
        assertThat(successCaptured)
            .allMatch(notification ->
                notification.getStatus().equals(NotificationStatus.SUCCESS)
        );
    }

    @Test
    @DisplayName("FCM 서버 에러 발생 시 엔티티 상태가 FAIL로 업데이트된다")
    void verify_notification_status_fail() {
        //given
        List<Long> userIds = List.of(1L, 2L);
        String title = "버스킹 알림 제목";
        String body = "버스킹 알림 내용";
        Long buskingId = 100L;

        given(fcmTokenService.getTokenMapByUserIds(anyList()))
                .willReturn(Map.of(1L, "token1", 2L, "token2"));

        given(senderPort.send(anyString(), anyString(), anyLong(), anyList()))
                .willReturn(CompletableFuture.failedFuture(new RuntimeException("FCM 서버 예외 발생")));

        //when
        notificationService.sendBulkNotification(title, body, buskingId, userIds);

        //then
        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);

        verify(bulkNotificationRepository).bulkUpsert(captor.capture());

        List<Notification> captured = captor.getValue();
        assertThat(captured).hasSize(2);
        assertThat(captured).allMatch(
                notification -> notification.getStatus().equals(NotificationStatus.FAIL)
        );
    }

    @Test
    @DisplayName("재시도 횟수 증가 로직 수행 시 retryCount가 증가한다")
    void increment_retry_count() {
        //given
        List<Long> notificationIds = List.of(1L, 2L);
        List<Notification> notifications = List.of(
                createNotification(1L, "제목1", "내용1", 1234L, 100L),
                createNotification(2L, "제목2", "내용2", 1234L, 100L)
        );
        given(notificationQueryService.findAllByIds(notificationIds))
                .willReturn(notifications);

        //when
        notificationService.incrementRetryCounts(notificationIds);
        notificationService.incrementRetryCounts(notificationIds);
        notificationService.incrementRetryCounts(notificationIds);

        //then
        for (Notification notification : notifications) {
            assertThat(notification.getRetryCount()).isEqualTo(3);
        }
    }

    private Notification createNotification(Long notificationId, String title, String body, Long receiverId, Long buskingId) {
        return Notification.create(
                () -> notificationId,
                title,
                body,
                receiverId,
                buskingId
        );
    }
}