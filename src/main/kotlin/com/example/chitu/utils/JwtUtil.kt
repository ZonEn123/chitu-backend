package com.example.chitu.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration:86400000}")
    private var expirationTime: Long = 86400000L

    // 缓存密钥对象，避免每次解析重复创建
    private val signKey by lazy { Keys.hmacShaKeyFor(secretKey.toByteArray()) }

    fun generateToken(userId: Long): String {
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(signKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUserId(token: String): Long {
        val claims = Jwts.parserBuilder()
            .setSigningKey(signKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject.toLong()
    }

    fun isTokenExpired(token: String): Boolean {
        val claims = Jwts.parserBuilder()
            .setSigningKey(signKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.expiration.before(Date())
    }

    fun validateToken(token: String): Boolean {
        return try {
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
}