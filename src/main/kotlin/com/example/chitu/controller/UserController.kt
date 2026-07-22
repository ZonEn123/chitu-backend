package com.example.chitu.controller

import com.example.chitu.dto.UpdateProfileRequest
import com.example.chitu.dto.UserProfileResponse
import com.example.chitu.service.UserService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @PutMapping("/profile")
    fun updateProfile(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: UpdateProfileRequest   // ✅ 使用具体 DTO
    ): Map<String, Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return mapOf(
                "code" to 401,
                "message" to "Token 无效或已过期"
            )
        }

        val userId = jwtUtil.extractUserId(token)
        val success = userService.updateUserProfile(userId, request)

        return if (success) {
            mapOf(
                "code" to 200,
                "message" to "更新成功"
            )
        } else {
            mapOf(
                "code" to 400,
                "message" to "更新失败"
            )
        }
    }

    @GetMapping("/profile")
    fun getProfile(
        @RequestHeader("Authorization") authorization: String
    ): Map<String, Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return mapOf(
                "code" to 401,
                "message" to "Token 无效或已过期"
            )
        }

        val userId = jwtUtil.extractUserId(token)
        val profile = userService.getUserProfile(userId)

        return if (profile != null) {
            mapOf(
                "code" to 200,
                "message" to "success",
                "data" to profile
            )
        } else {
            mapOf(
                "code" to 404,
                "message" to "用户不存在"
            )
        }
    }
}