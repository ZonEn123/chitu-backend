package com.example.chitu.controller

import com.example.chitu.dto.LoginRequest
import com.example.chitu.dto.LoginResponse
import com.example.chitu.dto.RegisterRequest
import com.example.chitu.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Map<String, Any>> {
        val user = userService.register(request)
        return if (user != null) {
            ResponseEntity.ok(mapOf(
                "code" to 200,
                "message" to "注册成功",
                "data" to mapOf("userId" to user.userId, "phone" to user.phone)
            ))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "code" to 400,
                "message" to "手机号已存在"
            ))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, Any>> {
        val user = userService.login(request)
        return if (user != null) {
            val token = userService.generateToken(user.userId!!)
            ResponseEntity.ok(mapOf(
                "code" to 200,
                "message" to "登录成功",
                "data" to LoginResponse(
                    token = token,
                    userId = user.userId!!,
                    phone = user.phone,
                    role = user.role
                )
            ))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf(
                "code" to 401,
                "message" to "手机号或密码错误，或账号已禁用"
            ))
        }
    }
}