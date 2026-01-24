package ticket.reserve.notification.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    @DisplayName("알림 생성 요청을 받으면 DB에 저장하고 저장된 객체를 반환한다")
    void createAndSendNotification_success() {
        //given: userId가 1234인 사용자에게 1번 게시글에 대한 알림 발송
        NotificationRequestDto request = new NotificationRequestDto("아이유 버스킹", "아이유 버스킹이 광화문에서 진행됩니다!", 1L, 1234L);
        given(senderPort.send(any(), any())).willReturn(new NotificationResult(true, null));

        //when
        NotificationResponseDto response = notificationService.createAndSend(request);

        //then
        assertThat(response.message()).isEqualTo("아이유 버스킹이 광화문에서 진행됩니다!");
        verify(senderPort, times(1)).send(any(Notification.class), anyString());
        verify(notificationCrudService, times(1)).save(any(Notification.class));
        verify(redisService, never()).addFailedNotification(any());
    }

    @Test
    @DisplayName("알림 발송 실패 시 DB에 저장되지 않고 Redis에 기록되어야 한다")
    void send_fail_noSaveDB_saveRedis() {
        //given
        NotificationRequestDto request = new NotificationRequestDto("아이유 버스킹", "아이유 버스킹이 광화문에서 진행됩니다!", 1L, 1234L);
        doThrow(new RuntimeException("API 에러")).when(senderPort).send(any(), any());

        //when
        notificationService.createAndSend(request);

        //then
        verify(notificationCrudService, never()).save(any());
        verify(redisService, times(1))
                .addFailedNotification(any(NotificationRetryDto.class));
    }
}
