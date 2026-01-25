package ticket.reserve.notification.domain.fcmtoken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.notification.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm_token")
public class FcmToken extends BaseTimeEntity {

    @Id
    @Column(name = "fcm_token_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
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

    public void updateFcmToken(String newFcmToken) {
        this.fcmToken = newFcmToken;
    }
}
