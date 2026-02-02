package ticket.reserve.core.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // GLOBAL
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL001", "권한을 찾지 못했습니다."),

    // USER-SERVICE
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER001", "사용자를 찾지 못했습니다."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "USER002", "아이디 또는 비밀번호가 틀렸습니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER003", "포인트가 존재하지 않습니다."),

    // BUSKING-SERVICE
    BUSKING_NOT_FOUND(HttpStatus.NOT_FOUND, "BUSKING001", "공연을 찾지 못했습니다."),
    BUSKING_CREATED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BUSKING002", "공연 생성 중 오류가 발생했습니다."),

    // INVENTORY-SERVICE
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "INVENTORY001", "좌석을 찾지 못했습니다."),
    INVENTORY_EXCEED(HttpStatus.BAD_REQUEST, "INVENTORY002", "해당 이벤트에 더이상 좌석을 생성할 수 없습니다."),
    INVENTORY_HOLD_FAIL(HttpStatus.BAD_REQUEST, "INVENTORY003", "좌석 선점에 실패했습니다."),
    INVENTORY_CONFIRM_FAIL(HttpStatus.BAD_REQUEST, "INVENTORY004", "선점 상태가 아닌 좌석의 승인을 시도하여 실패했습니다."),
    INVENTORY_RELEASE_FAIL(HttpStatus.BAD_REQUEST, "INVENTORY005", "선점 상태가 아닌 좌석의 릴리즈를 시도하여 실패했습니다."),

    // PAYMENT-SERVICE
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_CONFIRMED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT002", "결제 승인 중 오류가 발생했습니다."),


    // RESERVATION-SERVICE
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION001", "예매 정보를 찾을 수 없습니다."),
    RESERVATION_EXPIRED_ERROR(HttpStatus.BAD_REQUEST, "RESERVATION002", "예매 만료 이벤트 처리 중 오류가 발생했습니다."),
    NOT_ALLOWED(HttpStatus.BAD_REQUEST, "RESERVATION003", "대기열을 통과하지 않은 사용자입니다."),
    QUEUE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION004", "대기열에 존재하지 않는 사용자입니다."),

    // IMAGE-SERVICE
    IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE001", "이미지 업로드 중 오류가 발생했습니다."),
    NOT_ALLOWED_EXT(HttpStatus.BAD_REQUEST, "IMAGE002", "허용되지 않는 확장자입니다."),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "IMAGE003", "파일 크기는 5MB를 초과할 수 없습니다."),
    NOT_FOUND_BLOB_CONTAINER(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE004", "Azure Blob container가 존재하지 않습니다. 애플리케이션을 시작하기 전에 만들어주세요."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "IMAGE005", "파일명이 유효하지 않습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE006", "이미지를 찾지 못했습니다."),
    IMAGE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE007", "이미지 삭제 중 오류가 발생했습니다."),

    // NOTIFICATION-SERVICE
    NOT_FOUND_FCM_TOKEN(HttpStatus.NOT_FOUND, "NOTIFICATION001", "사용자 기기 FCM_TOKEN을 찾을 수 없습니다."),

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
