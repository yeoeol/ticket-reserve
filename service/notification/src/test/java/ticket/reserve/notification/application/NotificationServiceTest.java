package ticket.reserve.notification.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.application.dto.request.NotificationRequestDto;
import ticket.reserve.notification.domain.Notification;
import ticket.reserve.notification.domain.repository.NotificationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private IdGenerator idGenerator;

    @Test
    @DisplayName("알림 생성 요청을 받으면 DB에 저장하고 저장된 객체를 반환한다")
    void createNotification_success() {
        //given: userId가 1234인 사용자에게 1번 게시글에 대한 알림 발송
        NotificationRequestDto request = new NotificationRequestDto("아이유 버스킹", "아이유 버스킹이 광화문에서 진행됩니다!", 1L, 1234L);

        Notification notification = request.toEntity(idGenerator);
        given(notificationRepository.save(any())).willReturn(notification);

        //when
        NotificationResponseDto response = notificationService.create(request);

        //then
        assertThat(response.getMessage()).isEqualTo("아이유 버스킹이 광화문에서 진행됩니다!");
        verify(notificationRepository, times(1)).save(any());
    }
}
