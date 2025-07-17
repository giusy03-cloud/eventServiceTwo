package com.dipartimento.eventservice.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "erygihpoihviurghnferghrvieuvhnoutigrjg"; // DEVE essere IDENTICO a UserService

    private static final long EXPIRATION_TIME = 86400000;

    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public static String extractUserRole(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("role");
    }

    public static Long extractUserId(String token) {
        Object idClaim = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("userId");
        if (idClaim instanceof Integer) {
            return ((Integer) idClaim).longValue();
        } else if (idClaim instanceof Long) {
            return (Long) idClaim;
        } else if (idClaim instanceof String) {
            return Long.valueOf((String) idClaim);
        } else {
            throw new IllegalArgumentException("ID utente non valido nel token");
        }
    }

}
