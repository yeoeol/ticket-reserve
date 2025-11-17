package ticket.reserve.user.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final List<String> permitUris = List.of(
            // user-service
            "/users", "/users/register", "/users/login", "/css/**", "/users/css/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String requestURI = request.getRequestURI();
        return permitUris.stream().anyMatch(
                uri -> antPathMatcher.match(uri, requestURI)
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader("X-USER-ID");
        String userRole = request.getHeader("X-User-Roles");

        if (userId != null && userRole != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userRole));
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
