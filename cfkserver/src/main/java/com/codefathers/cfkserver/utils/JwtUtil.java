package com.codefathers.cfkserver.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtUtil {
    private static String SECRET_KEY = "secret";

    public static String generateToken(String userDetails,String role) {
        return createToken(userDetails,role);
    }

    private static String createToken(String username,String role) {
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim("username",username)
                .claim("role",role)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String extractUsername(String token) {
        return (String) extractAllClaims(token).getBody().get("username");
    }

    public static Boolean validateToken(String token) {
        extractAllClaims(token);
        return true;
    }

    private static Jws<Claims> extractAllClaims(String token) {
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return claimsJws;
    }
}