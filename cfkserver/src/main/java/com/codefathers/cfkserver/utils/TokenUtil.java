package com.codefathers.cfkserver.utils;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenUtil {
    public static boolean checkToken(HttpServletResponse response, HttpServletRequest request)
            throws ExpiredTokenException, InvalidTokenException {
        final String authorizationHeader = request.getHeader("Authentication");
        if (authorizationHeader != null && authorizationHeader.startsWith("cfk! ")) {
            String jwt = authorizationHeader.substring(5);
            try {
                return JwtUtil.validateToken(jwt);
            } catch (Exception e) {
                if (e instanceof ExpiredJwtException) {
                    throw new ExpiredTokenException("Expired Token");
                } else {
                    throw new InvalidTokenException("Invalid Token");
                }
            }
        } else {
            return false;
        }
    }

    public static String getUsernameFromToken(HttpServletRequest request)
            throws ExpiredTokenException, InvalidTokenException {
        final String authorizationHeader = request.getHeader("Authentication");
        if (authorizationHeader != null && authorizationHeader.startsWith("cfk! ")) {
            String jwt = authorizationHeader.substring(5);
            try {
                return JwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                if (e instanceof ExpiredJwtException) {
                    throw new ExpiredTokenException("Expired Token");
                } else {
                    throw new InvalidTokenException("Invalid Token");
                }
            }
        } else {
            throw new InvalidTokenException("Invalid Token");
        }
    }
}
