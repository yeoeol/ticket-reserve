package ticket.reserve.core.log.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();

        log.info("Request URI: {} | Method: {}", request.getRequestURI(), request.getMethod());

        MDC.put("requestStartTime", String.valueOf(startTime));
        MDC.put("requestUri", request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        long endTime = System.currentTimeMillis();
        long duration = endTime - Long.parseLong(MDC.get("requestStartTime"));

        log.info("Response Status: {} | Duration: {} ms", response.getStatus(), duration);

        MDC.remove("requestStartTime");
        MDC.remove("requestUri");
    }
}
