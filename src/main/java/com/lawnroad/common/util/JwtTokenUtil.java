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
public class  JwtTokenUtil {
//정수만의초강력비밀키정수만의초강력비밀키
    private final String SECRET_KEY = "sdkfjkdljfweifhaghghfkgdjkfhkdsjhfuehfegfdhfgsdhfhjhgshd"; // 최소 256bit
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    public String generateAccessToken(String clientId,Long no,String role,String nickname) {
        System.out.println("Access Token 발급!");
        return Jwts.builder()
                .setSubject(clientId)
                .claim("no", no)

                .claim("role",role)
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateRefreshToken(String clientId) {
        System.out.println("refresh 토큰 Token 발급!");
        return Jwts.builder()
                .setSubject(clientId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void storeRefreshToken(String clientId, String refreshToken) {
        refreshTokenStore.put(clientId, refreshToken);
    }

    public boolean isRefreshTokenValid(String clientId, String refreshToken) {
        return refreshToken.equals(refreshTokenStore.get(clientId));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public String getClientIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public Long getUserNoFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

//        System.out.println("claims 전체: " + claims);
//        System.out.println("claims.get(\"no\"): " + claims.get("no"));
        return claims.get("no", Long.class);
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

    // 필요하면 nickname, role 추출 메서드도 만들면 편함
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public String getNicknameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("nickname", String.class);
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}
