package ticket.reserve.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // USER-SERVICE
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER001", "사용자를 찾지 못했습니다."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "USER002", "아이디 또는 비밀번호가 틀렸습니다."),

    // EVENT-SERVICE
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT001", "이벤트를 찾지 못했습니다."),
    EVENT_CREATED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EVENT002", "이벤트 생성 중 오류가 발생했습니다."),

    // INVENTORY-SERVICE
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "INVENTORY001", "좌석을 찾지 못했습니다."),
    INVENTORY_EXCEED(HttpStatus.BAD_REQUEST, "INVENTORY002", "해당 이벤트에 더이상 좌석을 생성할 수 없습니다."),
    INVENTORY_HOLD_FAIL(HttpStatus.BAD_REQUEST, "INVENTORY003", "좌석 선점에 실패했습니다."),

    // PAYMENT-SERVICE
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_CONFIRMED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT002", "결제 승인 중 오류가 발생했습니다."),


    // RESERVATION-SERVICE
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION001", "예매 정보를 찾을 수 없습니다."),
    RESERVATION_EXPIRED_ERROR(HttpStatus.BAD_REQUEST, "RESERVATION002", "예매 만료 이벤트 처리 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
