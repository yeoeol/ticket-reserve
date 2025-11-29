package ticket.reserve.reservation.application.dto.response;

import lombok.Builder;

@Builder
public record QueueStatusResponseDto(
        String status,
        Long rank
) {
    public static QueueStatusResponseDto active() {
        return QueueStatusResponseDto.builder()
                .status("ACTIVE")
                .rank(0L)
                .build();
    }

    public static QueueStatusResponseDto waiting(Long rank) {
        return QueueStatusResponseDto.builder()
                .status("WAITING")
                .rank(rank)
                .build();
    }
}
