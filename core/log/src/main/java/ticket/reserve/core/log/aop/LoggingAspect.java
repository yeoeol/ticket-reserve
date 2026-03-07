package ticket.reserve.core.log.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // 모든 서비스의 application(Service) 계층 로그
    @Pointcut("execution(* ticket.reserve..presentation..*Controller.*(..)) || " +
                "execution(* ticket.reserve..application..*Service.*(..))")
   public void loggingTarget() {}

    @Before("loggingTarget()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[Method Start] {}.{} | Args: {}",
                className, methodName, Arrays.toString(args)
        );
    }

    @AfterReturning(pointcut = "loggingTarget()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[Method End] {}.{} | Return: {}", className, methodName, result);
    }

    @AfterThrowing(pointcut = "loggingTarget()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("[Method Exception] {}.{} | Message: {}",
                className, methodName, ex.getMessage()
        );
    }

    @Before("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void setScheduleTraceId() {
        if (MDC.get("traceId") == null) {
            String scheduleId = "SCHED-" + UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", scheduleId);
        }
    }

    @After("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void clearScheduleTraceId() {
        MDC.remove("traceId");
    }
}
