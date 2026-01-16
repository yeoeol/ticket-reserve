package ticket.reserve.gateway.infrastructure.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ticket.reserve.gateway.infrastructure.security.util.JwtProvider;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtCookieGatewayFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtProvider jwtProvider;
    private final AntPathMatcher antPathMatcher;
    private final ServerAuthenticationEntryPoint authenticationEntryPoint;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public static final List<String> permitUris = List.of(
            // user-service
            "/", "/users/register", "/users/login",
            "/api/users/register", "/api/users/login",
            // event-service
            "/events", "/events/*",
            // static resources
            "/users/css/**", "/events/css/**", "/inventories/css/**",
            "/admin/images/**"
    );

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String accessToken = extractToken(request);

            if (accessToken != null && jwtProvider.isJwtValid(accessToken)) {
                return checkBlacklistAndPass(exchange, chain, accessToken);
            }

            // 허용된 경로라면, JWT 검증 로직을 건너뛰고 바로 다음 필터로 진행
            if (isPermitted(path)) {
                return chain.filter(exchange);
            }

            return authenticationEntryPoint.commence(exchange, new AuthenticationException("JWT token is not valid") {});
        };
    }

    private Mono<Void> checkBlacklistAndPass(ServerWebExchange exchange, GatewayFilterChain chain, String accessToken) {
        return redisTemplate.hasKey("BL:"+accessToken)
                .flatMap(isBlacklisted -> {
                    if (isBlacklisted) {
                        return authenticationEntryPoint.commence(exchange, new AuthenticationException("Logged out token") {});
                    }

                    return passWithHeader(exchange, chain, accessToken);
                });
    }

    private Mono<Void> passWithHeader(ServerWebExchange exchange, GatewayFilterChain chain, String accessToken) {
        String userId = jwtProvider.getUserIdFromJwt(accessToken);
        String roles = jwtProvider.getRolesFromJwt(accessToken);

        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                .header("X-USER-ID", userId)
                .header("X-USER-ROLES", roles)
                .build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    private String  extractToken(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst("accessToken");

        return (cookie != null) ? cookie.getValue() : null;
    }

    private boolean isPermitted(String path) {
        return permitUris.stream()
                .anyMatch(pattern -> antPathMatcher
                        .match(pattern, path));
    }

}
