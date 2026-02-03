package ticket.reserve.notification.application.dto.response;

import com.google.common.base.Strings;
import lombok.Builder;

import java.util.List;

@Builder
public record NotificationBatchResponseDto(
        List<NotificationSendResponseDto> responses,
        int successCount,
        int failureCount
) {
    public static NotificationBatchResponseDto of(List<NotificationSendResponseDto> responses, int successCount, int failureCount) {
        return NotificationBatchResponseDto.builder()
                .responses(responses)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

    @Builder
    public record NotificationSendResponseDto(
            String messageId,
            String errorCode,
            String errorMessage
    ) {
        public static NotificationSendResponseDto of(String messageId, String errorCode, String errorMessage) {
            return NotificationSendResponseDto.builder()
                    .messageId(messageId)
                    .errorCode(errorCode)
                    .errorMessage(errorMessage)
                    .build();
        }

        public boolean isSuccessful() {
            return !(this.messageId == null || this.messageId.isEmpty());
        }
    }
}
