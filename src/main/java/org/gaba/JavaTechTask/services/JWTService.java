package org.gaba.JavaTechTask.services;

import com.amazonaws.services.applicationdiscovery.model.AuthorizationErrorException;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.gaba.JavaTechTask.entities.Account;
import org.gaba.JavaTechTask.entities.Authority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class JWTService {

    private final byte[] secret;
    private final Long expirationTime;

    private static final String ID_CLAIM = "ID";
    private static final String USERNAME_CLAIM = "username";
    private static final String AUTHORITIES_CLAIM = "authorities";

    public JWTService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") String expirationTime) {
        this.secret = secret.getBytes();
        this.expirationTime = Long.parseLong(expirationTime) * 1000;
    }

    public String extractUsername(String token) {
        return getClaims(token).get(USERNAME_CLAIM, String.class);
    }

    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        List<String> authorities = getClaims(token).get(AUTHORITIES_CLAIM, List.class);
        return authorities.stream().map(SimpleGrantedAuthority::new).toList();
    }
    public String generateToken(Account account) {

        var claims = new HashMap<String, Object>();
        claims.put(ID_CLAIM, account.getId().toString());
        claims.put(USERNAME_CLAIM, account.getUsername());
        claims.put(AUTHORITIES_CLAIM, account.getAuthorities());

        var creationTime = new Date();
        var expired = new Date(creationTime.getTime() + expirationTime);


        return "Bearer " + Jwts.builder()
                .addClaims(claims)
                .setSubject(account.getUsername())
                .setIssuedAt(creationTime)
                .setExpiration(expired)
                .signWith(Keys.hmacShaKeyFor(secret))
                .compact();
    }

    public boolean validateToken(String token) {

        if(token == null || !token.startsWith("Bearer "))
            return false;

        token = token.substring(7);

        return !expired(token);
    }

    private boolean expired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {

        if(token == null) {
            throw new AuthorizationErrorException("Invalid token");
        }

        if(token.startsWith("Bearer "))
            token = token.substring(7);

        return Jwts.parserBuilder().setSigningKey(secret)
                .build().parseClaimsJws(token).getBody();
    }

    public String extractId(String token) {
        return getClaims(token).get(ID_CLAIM, String.class);
    }
}
