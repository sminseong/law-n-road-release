package com.lawnroad.common.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;


import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class JwtTokenUtil {

    private final String SECRET_KEY = "정수만의초강력비밀키정수만의초강력비밀키"; // 최소 256bit
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void storeRefreshToken(String email, String refreshToken) {
        refreshTokenStore.put(email, refreshToken);
    }

    public boolean isRefreshTokenValid(String email, String refreshToken) {
        return refreshToken.equals(refreshTokenStore.get(email));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public void printPayload(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        System.out.println("📦 JWT Payload 내용:");
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            System.out.println("  " + entry.getKey() + " : " + entry.getValue());
        }
    }






}
