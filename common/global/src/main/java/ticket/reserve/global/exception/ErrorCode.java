package ticket.reserve.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER001", "사용자를 찾지 못했습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
