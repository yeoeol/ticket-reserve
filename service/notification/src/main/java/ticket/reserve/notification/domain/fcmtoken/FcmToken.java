package ticket.reserve.notification.domain.fcmtoken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.tsid.IdGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm_token")
public class FcmToken {

    @Id
    @Column(name = "fcm_token_id")
    private Long id;

    @Column(unique = true)
    private Long userId;

    @Column(unique = true)
    private String fcmToken;

    @Builder(access = AccessLevel.PRIVATE)
    private FcmToken(IdGenerator idGenerator, Long userId, String fcmToken) {
        this.id = idGenerator.nextId();
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public static FcmToken create(IdGenerator idGenerator, Long userId, String fcmToken) {
        return FcmToken.builder()
                .idGenerator(idGenerator)
                .userId(userId)
                .fcmToken(fcmToken)
                .build();
    }
}
