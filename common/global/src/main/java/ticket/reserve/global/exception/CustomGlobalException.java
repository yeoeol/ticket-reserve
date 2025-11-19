package ticket.reserve.global.exception;

import lombok.Getter;

@Getter
public class CustomGlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomGlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomGlobalException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
