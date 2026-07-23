package com.example.chitu.controller

import com.example.chitu.dto.UserSettingResponse
import com.example.chitu.service.UserSettingService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserSettingController(
    private val userSettingService: UserSettingService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/setting")
    fun getSetting(
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
        val setting = userSettingService.getUserSetting(userId)

        return mapOf(
            "code" to 200,
            "message" to "success",
            "data" to setting
        )
    }
}