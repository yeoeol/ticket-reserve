package ticket.reserve.notification.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.application.dto.request.NotificationRetryDto;
import ticket.reserve.notification.application.dto.response.NotificationResponseDto;
import ticket.reserve.notification.application.dto.response.NotificationResult;
import ticket.reserve.notification.application.port.out.SenderPort;
import ticket.reserve.notification.domain.notification.Notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SenderPort senderPort;

    @Mock
    private RedisService redisService;

    @Mock
    private NotificationCrudService notificationCrudService;

    @Mock
    private FcmTokenService fcmTokenService;

    private static final int MAX_RETRY_COUNT = 5;

    @Test
    @DisplayName("알림 생성 요청을 받으면 DB에 저장하고 저장된 객체를 반환한다")
    void createAndSendNotification_success() {
        //given: userId가 1234인 사용자에게 1번 게시글에 대한 알림 발송
        NotificationRequestDto request = new NotificationRequestDto("아이유 버스킹", "아이유 버스킹이 광화문에서 진행됩니다!", 1L, 1234L, 0);
        given(fcmTokenService.getTokenByUserId(1234L)).willReturn("testFcmToken");
        given(senderPort.send(any(), any())).willReturn(NotificationResult.successResult());

        //when
        NotificationResponseDto response = notificationService.createAndSend(request);

        //then
        assertThat(response.message()).isEqualTo("아이유 버스킹이 광화문에서 진행됩니다!");
        assertThat(response.result().isSuccess()).isTrue();
        assertThat(response.result().errorCode()).isNull();
        verify(senderPort, times(1)).send(any(Notification.class), anyString());
        verify(notificationCrudService, times(1)).save(any(Notification.class));
        verify(redisService, never()).addFailedNotification(any(), anyLong());
    }

    @Test
    @DisplayName("알림 발송 실패 시 DB에 저장되지 않고 Redis에 기록되어야 한다")
    void send_fail_noSaveDB_saveRedis() {
        //given
        NotificationRequestDto request = new NotificationRequestDto("아이유 버스킹", "아이유 버스킹이 광화문에서 진행됩니다!", 1L, 1234L, 0);
        given(fcmTokenService.getTokenByUserId(any())).willReturn("testFcmToken");
        given(senderPort.send(any(), any()))
                .willReturn(NotificationResult.failResult(500));

        //when
        NotificationResponseDto response = notificationService.createAndSend(request);

        //then
        assertThat(response.result().isSuccess()).isFalse();
        assertThat(response.result().errorCode()).isEqualTo(500);
        verify(notificationCrudService, never()).save(any());
        verify(redisService, times(1))
                .addFailedNotification(any(NotificationRetryDto.class), anyLong());
    }

    @Test
    @DisplayName("알림 발송 최대 재시도 횟수 미만일 시 Redis에 기록되고 재시도 횟수가 1 증가한다")
    void send_retry_saveRedis_increse_retryCount() {
        //given
        NotificationRequestDto request = new NotificationRequestDto("아이유 버스킹", "아이유 버스킹이 광화문에서 진행됩니다!", 1L, 1234L, 1);
        given(fcmTokenService.getTokenByUserId(any())).willReturn("testFcmToken");
        given(senderPort.send(any(), any()))
                .willReturn(NotificationResult.failResult(500));

        //when
        NotificationResponseDto response1 = notificationService.createAndSend(request);

        //then
        assertThat(response1.result().isSuccess()).isFalse();
        assertThat(response1.result().errorCode()).isEqualTo(500);
        verify(notificationCrudService, never()).save(any());
        verify(redisService, times(1))
                .addFailedNotification(any(NotificationRetryDto.class), anyLong());
    }

    @Test
    @DisplayName("알림 발송 최대 재시도 횟수 이상일 시 ReidService를 호출하지 않고 로그 출력 후 즉시 종료된다")
    void send_max_retry() {
        //given
        NotificationRequestDto request = new NotificationRequestDto("아이유 버스킹", "아이유 버스킹이 광화문에서 진행됩니다!", 1L, 1234L, MAX_RETRY_COUNT-1);
        given(fcmTokenService.getTokenByUserId(any())).willReturn("testFcmToken");
        given(senderPort.send(any(), any()))
                .willReturn(NotificationResult.failResult(500));

        //when
        NotificationResponseDto response = notificationService.createAndSend(request);

        //then
        assertThat(response.result().isSuccess()).isFalse();
        assertThat(response.result().errorCode()).isEqualTo(500);
        verify(notificationCrudService, never()).save(any());
        verify(redisService, never()).addFailedNotification(any(), anyLong());
    }

    @Test
    @DisplayName("알림 발송 알림 발송 실패 시 retryCount가 1 증가하여 Redis에 저장되어야 한다")
    void should_incerease_retryCount_onFailure() {
        //given
        long receiverId = 1234L;
        NotificationRequestDto request = new NotificationRequestDto(
                "제목", "내용", 1L, receiverId, 1
        );

        given(fcmTokenService.getTokenByUserId(receiverId)).willReturn("testFcmToken");
        given(senderPort.send(any(), any()))
                .willReturn(NotificationResult.failResult(500));

        ArgumentCaptor<NotificationRetryDto> retryDtoCaptor = ArgumentCaptor.forClass(NotificationRetryDto.class);
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);

        //when
        notificationService.createAndSend(request);

        //then
        verify(redisService, times(1))
                .addFailedNotification(retryDtoCaptor.capture(), delayCaptor.capture());
        verify(notificationCrudService, never()).save(any());

        NotificationRetryDto capturedRetryDto = retryDtoCaptor.getValue();
        assertThat(capturedRetryDto.retryCount()).isEqualTo(request.retryCount()+1);
        assertThat(capturedRetryDto.receiverId()).isEqualTo(receiverId);

        assertThat(delayCaptor.getValue())
                .isEqualTo((long) Math.pow(2, request.retryCount()+1) * 60);
    }
}
