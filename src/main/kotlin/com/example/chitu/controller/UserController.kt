package com.example.chitu.controller

import com.example.chitu.dto.ApiResponse
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

    @GetMapping("/profile")
    fun getProfile(
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<UserProfileResponse> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val userId = jwtUtil.extractUserId(token)
        val profile = userService.getUserProfile(userId)
        return if (profile != null) {
            ApiResponse.success(profile)
        } else {
            ApiResponse.error(404, "用户不存在")
        }
    }

    @PutMapping("/profile")
    fun updateProfile(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: UpdateProfileRequest
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val userId = jwtUtil.extractUserId(token)
        val success = userService.updateUserProfile(userId, request)
        return if (success) {
            ApiResponse.success(message = "更新成功")
        } else {
            ApiResponse.error(400, "更新失败")
        }
    }
}
