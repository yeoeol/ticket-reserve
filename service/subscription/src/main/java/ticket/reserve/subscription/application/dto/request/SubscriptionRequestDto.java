package ticket.reserve.subscription.application.dto.request;

import jakarta.validation.constraints.NotNull;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.subscription.domain.Subscription;
import ticket.reserve.subscription.domain.enums.SubscriptionStatus;

import java.time.LocalDateTime;

public record SubscriptionRequestDto(
        @NotNull(message = "{busking.id.not_null}")
        Long buskingId,
        @NotNull(message = "{user.id.not_null}")
        Long userId,
        @NotNull(message = "{busking.startTime.not_null}")
        LocalDateTime startTime
) {
        public Subscription toEntity(IdGenerator idGenerator) {
                return Subscription.create(
                        idGenerator,
                        this.userId,
                        this.buskingId,
                        this.startTime,
                        SubscriptionStatus.ACTIVATED
                );
        }
}
