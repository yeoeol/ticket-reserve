package ticket.reserve.notification.domain.notification.enums;

public enum NotificationStatus  {
    PENDING,            // 발송 대기
    SUCCESS,
    FAIL,               // 재시도 대상
    PERMANENT_FAILURE   // 재시도 중단
}
