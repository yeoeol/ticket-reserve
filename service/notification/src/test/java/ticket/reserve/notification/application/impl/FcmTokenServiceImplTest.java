package ticket.reserve.notification.application.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.fcmtoken.FcmToken;
import ticket.reserve.notification.domain.fcmtoken.repository.FcmTokenRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceImplTest {

    @InjectMocks
    private FcmTokenServiceImpl fcmTokenService;

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @Test
    @DisplayName("userId 리스트로 FCM TOKEN을 조회한 후 각 userId와 FcmToken(String)을 매핑하여 반환한다.")
    void get_tokenMap_by_userIds_success() {
        //given
        List<Long> userIds = List.of(10L, 20L, 30L);
        List<FcmToken> tokens = List.of(
                createFcmToken(1L, 10L, "fcmToken1"),
                createFcmToken(2L, 20L, "fcmToken2"),
                createFcmToken(3L, 30L, "fcmToken3")
        );

        given(fcmTokenRepository.findByUserIdIn(userIds)).willReturn(tokens);

        //when
        Map<Long, String> tokenMap = fcmTokenService.getTokenMapByUserIds(userIds);

        //then
        assertThat(tokenMap).hasSize(3);
        assertThat(tokenMap.get(10L)).isEqualTo("fcmToken1");
        assertThat(tokenMap.get(20L)).isEqualTo("fcmToken2");
        assertThat(tokenMap.get(30L)).isEqualTo("fcmToken3");
    }

    private FcmToken createFcmToken(Long tokenId, Long userId, String fcmToken) {
        return FcmToken.create(
                () -> tokenId,
                userId,
                fcmToken
        );
    }
}