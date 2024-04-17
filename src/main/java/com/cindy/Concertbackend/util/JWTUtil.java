package com.cindy.Concertbackend.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cindy.Concertbackend.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTUtil {

    /** Token有效期限 (設定1天過期) */
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000;

    @Value("${jwt.secret-key}")
    private String secretKey;

    /**
     * 一、 獲取JWT簽名的密鑰
     */
    private SecretKey getSignInKey() {
        byte[] encodeKey = Base64.getEncoder().encode(secretKey.getBytes());
        return Keys.hmacShaKeyFor(encodeKey);
    }

    /**
     *二、 簽發Token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // 可以將使用者其他訊息存在JWT中
        claims.put("id", user.getId());

        return Jwts
                // 開始創建Jwt
                .builder()
                // 將聲明加到jwt
                .setClaims(claims)
                // 將用戶唯一指標
                .setSubject(user.getEmail())
                // 設定JWT創建的時間
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 設定JWT過期的時間
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // 透過SHA-256加密鑰匙對JWT進行簽名
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                // 創建JWT
                .compact();
    }

    /**
     * 三之一、 從JWT令牌中提取用戶email
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 三之二、驗證token是否過期
     */
    public boolean isTokenExpired(String token) {
        // Claims::getExpiration表示引用Claims物件中的getExpiration方法
        final Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate != null && expirationDate.before(new Date());
    }

    /**
     * 提取JWT令牌中的任何聲明（Claims），並通過提供的Function來解析
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 獲取令牌中所有的聲明將其解析
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                // 創建JwtParserBuilder物件，用於解析JWT
                .parserBuilder()
                // 拿到Key，並驗證JWT簽名是否有效
                .setSigningKey(getSignInKey())
                .build()
                // 使用建構好的JWT解析器解析Token
                .parseClaimsJws(token)
                // 得到Token裡面所有的claims(Claims物件)
                .getBody();
    }

}
