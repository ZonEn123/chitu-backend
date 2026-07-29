package com.example.chitu.controller

import com.example.chitu.service.DriverStatisticsService
import com.example.chitu.service.UserService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/statistics")
class DriverStatisticsController(
    private val driverStatisticsService: DriverStatisticsService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    /** 平台总体统计 */
    @GetMapping("/overview")
    fun overview(@RequestHeader("Authorization") auth: String): Map<String, Any> {
        if (!checkAdmin(auth)) return mapOf("code" to 403, "message" to "权限不足")
        return mapOf("code" to 200, "data" to driverStatisticsService.getPlatformStats())
    }

    /** 各司机统计列表 */
    @GetMapping("/drivers")
    fun drivers(@RequestHeader("Authorization") auth: String): Map<String, Any> {
        if (!checkAdmin(auth)) return mapOf("code" to 403, "message" to "权限不足")
        return mapOf("code" to 200, "data" to driverStatisticsService.getAllDriverStatistics())
    }

    private fun checkAdmin(auth: String): Boolean {
        try {
            val token = auth.replace("Bearer ", "")
            if (!jwtUtil.validateToken(token)) return false
            val userId = jwtUtil.extractUserId(token)
            val user = userService.getUserById(userId)
            return user != null && user.role == 1
        } catch (_: Exception) {
            return false
        }
    }
}
