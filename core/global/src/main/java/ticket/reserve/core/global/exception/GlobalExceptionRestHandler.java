package ticket.reserve.core.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionRestHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e.getErrorCode().name(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();

        if (errorMessage != null && errorMessage.startsWith("{") && errorMessage.endsWith("}")) {
            String key = errorMessage.substring(1, errorMessage.length() - 1);
            errorMessage = messageSource.getMessage(key, null, "유효하지 않은 입력입니다.", LocaleContextHolder.getLocale());
        }

        if (errorMessage == null) {
            errorMessage = "유효하지 않은 입력입니다.";
        }

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_INPUT", errorMessage));
    }
}
