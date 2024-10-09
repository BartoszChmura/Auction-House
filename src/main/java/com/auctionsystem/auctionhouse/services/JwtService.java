package com.auctionsystem.auctionhouse.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        log.info("Extracting username from token");
        String username = extractClaim(token, Claims::getSubject);
        log.info("Username from token: {}", username);

        return username;
    }

    public Date extractExpiration(String token) {
        log.info("Extracting expiration date from token");
        Date expiration = extractClaim(token, Claims::getExpiration);
        log.info("Expiration date from token: {}", expiration);

        return expiration;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.info("Extracting claim from token");
        Claims claims = extractAllClaims(token);
        T claim = claimsResolver.apply(claims);
        log.info("Claim from token extracted");

        return claim;
    }

    private Claims extractAllClaims(String token) {
        log.info("Extracting all claims from token");
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        log.info("All claims from token extracted");

        return claims;
    }

    private Boolean isTokenExpired(String token) {
        log.info("Checking if token is expired");
        Boolean isExpired = extractExpiration(token).before(new Date());
        log.info("Token is expired: {}", isExpired);

        return isExpired;
    }

    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, userDetails.getUsername());
        log.info("Token generated successfully");

        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        log.info("Creating token for subject: {}", subject);
        String token = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
        log.info("Token created successfully");

        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info("Validating token for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        Boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        log.info("Token is {} for user: {}", isValid ? "valid " : "invalid ", userDetails.getUsername());

        return isValid;
    }
}