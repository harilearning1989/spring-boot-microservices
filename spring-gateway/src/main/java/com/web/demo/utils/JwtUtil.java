package com.web.demo.utils;

import com.web.demo.exceptions.JwtProblemException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkey";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /*public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }*/

    public Claims validateAndGetClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException ex) {
            throw buildProblem(
                    "Token Expired",
                    "JWT token has expired",
                    HttpStatus.UNAUTHORIZED,
                    "/errors/token-expired"
            );

        } catch (MalformedJwtException ex) {
            throw buildProblem(
                    "Malformed Token",
                    "JWT token structure is invalid",
                    HttpStatus.BAD_REQUEST,
                    "/errors/malformed-token"
            );

        } catch (SignatureException ex) {
            throw buildProblem(
                    "Invalid Signature",
                    "JWT signature validation failed",
                    HttpStatus.UNAUTHORIZED,
                    "/errors/invalid-signature"
            );

        } catch (UnsupportedJwtException ex) {
            throw buildProblem(
                    "Unsupported Token",
                    "JWT token type is not supported",
                    HttpStatus.BAD_REQUEST,
                    "/errors/unsupported-token"
            );

        } catch (WeakKeyException ex) {
            throw buildProblem(
                    "Weak Signing Key",
                    "Server JWT signing key is not secure enough",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "/errors/weak-key"
            );

        } catch (IllegalArgumentException ex) {
            throw buildProblem(
                    "Invalid Token",
                    "JWT token is null or empty",
                    HttpStatus.BAD_REQUEST,
                    "/errors/invalid-token"
            );
        }
    }

    private RuntimeException buildProblem(String title,
                                          String detail,
                                          HttpStatus status,
                                          String type) {

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        problem.setType(URI.create(type));
        problem.setProperty("timestamp", Instant.now());

        return new JwtProblemException(problem);
    }

    public boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }
}

