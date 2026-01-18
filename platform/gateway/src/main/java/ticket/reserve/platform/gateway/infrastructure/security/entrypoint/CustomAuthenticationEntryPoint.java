package ticket.reserve.platform.gateway.infrastructure.security.entrypoint;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private static final String ADMIN_LOGIN_URL = "/users/register";
    private static final String USER_LOGIN_URL = "/users/register";

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/admin")) {
            return redirect(exchange, ADMIN_LOGIN_URL);
        } else {
            return redirect(exchange, USER_LOGIN_URL);
        }
    }

    private Mono<Void> redirect(ServerWebExchange exchange, String location) {
        String currentPath = exchange.getRequest().getURI().getPath();

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(location + "?redirectUri=" + currentPath));

        return response.setComplete();
    }
}
