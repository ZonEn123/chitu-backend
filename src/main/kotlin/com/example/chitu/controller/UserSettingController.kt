package com.example.chitu.controller

import com.example.chitu.dto.ApiResponse
import com.example.chitu.dto.UpdateSettingRequest
import com.example.chitu.service.UserSettingService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserSettingController(
    private val userSettingService: UserSettingService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/setting")
    fun getSetting(
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val userId = jwtUtil.extractUserId(token)
        val setting = userSettingService.getUserSetting(userId)
        return ApiResponse.success(setting)
    }

    @PutMapping("/setting")
    fun updateSetting(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: UpdateSettingRequest
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val userId = jwtUtil.extractUserId(token)
        val success = userSettingService.updateUserSetting(userId, request)
        return if (success) {
            ApiResponse.success(message = "保存成功")
        } else {
            ApiResponse.error(400, "保存失败")
        }
    }
}
