package ticket.reserve.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ticket.reserve.gateway.config.GatewaySecurityConfig;

import javax.crypto.SecretKey;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<Object> {

    public static final String[] permitUris = {
            // user-service
            "/users/", "/users/register", "/users/login"

    };

    private final SecretKey key;
    private final long expiration;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret,
                                   @Value("${jwt.expiration}") long expiration) {
        super(Object.class);
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            System.out.println("0");

            String path = request.getURI().getPath();
            boolean isPermitted = Arrays.stream(permitUris)
                    .anyMatch(pattern -> antPathMatcher
                            .match(pattern, path));

            // 허용된 경로라면, JWT 검증 로직을 건너뛰고 바로 다음 필터로 진행
            if (isPermitted) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }
            System.out.println("1");
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            System.out.println("2");
            String userId = getUserIdFromJwt(jwt);
            String roles = getRolesFromJwt(jwt);

            ServerHttpRequest newRequest = request.mutate()
                    .header("X-USER-ID", userId)
                    .header("X-User-Roles", roles)
                    .build();

            System.out.println("3");
            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }

    private String getUserIdFromJwt(String jwt) {
        return getClaims(jwt).getSubject();
    }

    private String getRolesFromJwt(String jwt) {
        return (String) getClaims(jwt).get("roles");
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;
        String subject = null;

        try {
            subject = getUserIdFromJwt(jwt);
        } catch (Exception e) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        System.out.println("[Error log] : " + err);
        return response.setComplete();
    }
}
