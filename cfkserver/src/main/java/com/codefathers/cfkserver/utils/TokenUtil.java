package com.codefathers.cfkserver.utils;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenUtil {
    // TODO: 7/18/2020 :|||||||||| midoonam ridam :)
    public static boolean checkToken(HttpServletResponse response, HttpServletRequest request)
            throws ExpiredTokenException, InvalidTokenException {
        final String authorizationHeader = request.getHeader("Authentication");
        if (authorizationHeader != null && authorizationHeader.startsWith("cfk! ")) {
            String jwt = authorizationHeader.substring(5);
            System.out.println(jwt);
            try {
                JwtUtil.extractUsername(jwt);
                return true;
            } catch (Exception e) {
                if (e instanceof ExpiredJwtException) {
                    throw new ExpiredTokenException("Expired Token");
                } else {
                    throw new InvalidTokenException("Invalid Token");
                }
            }
        } else {
            System.out.println("empty header");
            return false;
        }
    }

    public static String getUsernameFromToken(HttpServletRequest request)
            throws ExpiredTokenException, InvalidTokenException {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("anonymous ")) {
            String jwt = authorizationHeader.substring(10);
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
