package ticket.reserve.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static java.util.stream.Collectors.joining;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException e, Model model) {
        List<ErrorResponse.FieldErrorDetail> fieldErrors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldErrorDetail(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()
                ))
                .toList();
        String errorMessageList = fieldErrors.stream()
                .map(ErrorResponse.FieldErrorDetail::getReason)
                .collect(joining("\n"));

        String message = "입력 값 검증에 실패했습니다.";
        log.warn("Validation 실패: {} 건", fieldErrors.size());

        model.addAttribute("errorMessage", message);
        model.addAttribute("errorDetails", errorMessageList); // 개발 단계에서만 노출

        return "error/4xx";
    }

    @ExceptionHandler(CustomException.class)
    public String handleBaseException(CustomException exception, Model model) {
        log.warn("비즈니스 예외 발생: {}", exception.getMessage());

        ErrorCode errorCode = exception.getErrorCode();
        model.addAttribute("errorMessage", errorCode.getMessage());
        model.addAttribute("errorCode", "");

        return "error/error-page";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model) {
        log.error("알 수 없는 서버 에러 발생", e);

        model.addAttribute("errorMessage", "시스템 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        model.addAttribute("errorDetails", e.getMessage()); // 개발 단계에서만 노출

        return "error/500";
    }
}
