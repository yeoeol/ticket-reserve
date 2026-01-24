package ticket.reserve.notification.domain.fcmtoken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm_token")
public class FcmToken {

    @Id
    @Column(name = "fcm_token_id")
    private Long id;

    private Long userId;

    @Column(unique = true)
    private String fcmToken;
}
