package ticket.reserve.core.log.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ticket.reserve.core.log.interceptor.RequestLoggingInterceptor;

@Configuration
@ComponentScan(basePackages = "ticket.reserve.core.log")
@RequiredArgsConstructor
public class LogConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE);
    }
}
