package ticket.reserve.reservation.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedUser {

    /**
     * SpEL을 받을 속성 추가 (예: "#request.eventId")
     */
    String eventId() default "#request.eventId";

    /**
     * SpEL을 받을 속성 추가 (예: "#userId")
     */
    String userId() default "#userId";
}
