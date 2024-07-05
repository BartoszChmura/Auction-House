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
        log.info("Ekstrakcja nazwy użytkownika z tokenu");
        String username = extractClaim(token, Claims::getSubject);
        log.info("Nazwa użytkownika z tokenu: {}", username);
        return username;
    }

    public Date extractExpiration(String token) {
        log.info("Ekstrakcja daty wygaśnięcia z tokenu");
        Date expiration = extractClaim(token, Claims::getExpiration);
        log.info("Data wygaśnięcia z tokenu: {}", expiration);
        return expiration;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.info("Ekstrakcja roszczenia z tokenu");
        Claims claims = extractAllClaims(token);
        T claim = claimsResolver.apply(claims);
        log.info("Roszczenie z tokenu wyekstrahowane");
        return claim;
    }

    private Claims extractAllClaims(String token) {
        log.info("Ekstrakcja wszystkich roszczeń z tokenu");
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        log.info("Wszystkie roszczenia z tokenu wyekstrahowane");
        return claims;
    }

    private Boolean isTokenExpired(String token) {
        log.info("Sprawdzanie, czy token wygasł");
        Boolean isExpired = extractExpiration(token).before(new Date());
        log.info("Token wygasł: {}", isExpired);
        return isExpired;
    }

    public String generateToken(UserDetails userDetails) {
        log.info("Generowanie tokenu dla użytkownika: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, userDetails.getUsername());
        log.info("Token wygenerowany pomyślnie");
        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        log.info("Tworzenie tokenu dla podmiotu: {}", subject);
        String token = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
        log.info("Token stworzony pomyślnie");
        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info("Walidacja tokenu dla użytkownika: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        Boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        log.info("Token jest {}dla użytkownika: {}", isValid ? "ważny " : "nieważny ", userDetails.getUsername());
        return isValid;
    }
}