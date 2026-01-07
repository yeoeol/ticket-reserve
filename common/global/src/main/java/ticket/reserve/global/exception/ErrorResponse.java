package ticket.reserve.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    String code;
    String message;
    List<FieldErrorDetail> errors;

    @Getter
    @AllArgsConstructor
    static class FieldErrorDetail {
        String field;
        String value;
        String reason;
    }
}
