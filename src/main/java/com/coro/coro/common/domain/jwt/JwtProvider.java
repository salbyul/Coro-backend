package com.coro.coro.common.domain.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;
    private final UserDetailsService userDetailsService;
    private Key key;

    //    토근 유효시간 30분
    @SuppressWarnings("FieldCanBeLocal")
    private final long tokenValidTime = 30 * 60 * 1000L;


    @PostConstruct
    protected void init() {
        key = createKey();
    }

    public String generateAccessToken(final String userNickname) {
        Map<String, Object> claims = generateClaims(userNickname);
        Map<String, Object> header = generateHeader();

        Date now = new Date();
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> generateClaims(final String userNickname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nickname", userNickname);
        return claims;
    }

    private Map<String, Object> generateHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        header.put("sub", "USER");
        return header;
    }

    private Key createKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Authentication getAuthentication(final String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserNickname(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", null);
    }

    public String getUserNickname(final String token) {
        String subject = (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("nickname");
        log.info("subject: {}", subject);
        return subject;
    }

    public String extractToken(final HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization == null ? null : authorization.substring(authorization.indexOf(" ") + 1);
    }

    public boolean validateToken(final String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
