package co.com.pragma.api.security;


import co.com.pragma.secretsprovider.SecretsProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretsProvider secretsProvider;

    public JwtUtil(SecretsProvider secretsProvider) {
        this.secretsProvider = secretsProvider;
    }

/*
    public JwtUtil(@Value("${adapter.jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }
*/

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretsProvider.getJwtSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getRoleFromToken(String token) {
        return getAllClaimsFromToken(token).get("rol", String.class);
    }

    private Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public String getDocumentoFromToken(String token) {
        return getAllClaimsFromToken(token).get("documentoIdentidad", String.class);
    }

    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
