package ticket.reserve.user.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ticket.reserve.user.application.port.out.GenerateTokenPort;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class TokenAdapter implements GenerateTokenPort {

    private final SecretKey key;
    private final long expiration;

    public TokenAdapter(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.expiration}") long expiration
    ) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    @Override
    public String generateToken(Long userId, List<String> userRoles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("roles", userRoles) // 필요시 role 추가
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    @Override
    public long getRemainingTime(String jwt) {
        long nowTime = new Date().getTime();
        long expTime = getClaims(jwt).getExpiration().getTime();
        return expTime - nowTime;
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
