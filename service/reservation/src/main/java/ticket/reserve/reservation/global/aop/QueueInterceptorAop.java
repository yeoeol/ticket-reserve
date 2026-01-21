package ticket.reserve.reservation.global.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.reservation.global.annotation.AllowedUser;
import ticket.reserve.reservation.global.util.CustomSpringELParser;
import ticket.reserve.reservation.infrastructure.persistence.QueueService;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class QueueInterceptorAop {

    private final QueueService queueService;

    @Before(value = "@annotation(ticket.reserve.reservation.global.annotation.AllowedUser)")
    public void checkAllowed(final JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        AllowedUser allowedUser = method.getAnnotation(AllowedUser.class);

        Long buskingId = (Long) CustomSpringELParser.getDynamicValue(
                parameterNames, args, allowedUser.buskingId()
        );
        Long userId = (Long) CustomSpringELParser.getDynamicValue(
                parameterNames, args, allowedUser.userId()
        );

        // Active queue에 있는지 확인
        if (!queueService.isAllowed(buskingId, userId)) {
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }
    }
}
