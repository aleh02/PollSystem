package it.alessandrohan.pollsystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.alessandrohan.pollsystem.model.User;
import it.alessandrohan.pollsystem.web.dto.response.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    private final byte[] secretKeyBytes;
    private final long jwtExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secretKey,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.secretKeyBytes = Base64.getDecoder().decode(secretKey);
        this.jwtExpirationMs = expirationMinutes * 60_000;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public LoginResponse generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtExpirationMs);

        String token = Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(getKey())
                .compact();

        return new LoginResponse(token, expiresAt);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }
}
