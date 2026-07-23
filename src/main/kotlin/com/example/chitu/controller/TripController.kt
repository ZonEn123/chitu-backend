package com.example.chitu.controller

import com.example.chitu.dto.TripSyncRequest
import com.example.chitu.service.TripService
import com.example.chitu.service.UserService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/trips")
class TripController(
    private val tripService: TripService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    /**
     * 同步单条行程（Android 端调用）
     * 幂等设计：根据 clientId 去重
     */
    @PostMapping("/sync")
    fun syncTrip(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: TripSyncRequest
    ): Map<String, Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return mapOf("code" to 401, "message" to "Token 无效或已过期")
        }

        val userId = jwtUtil.extractUserId(token)
        val success = tripService.syncTrip(userId, request.clientId, request)

        return if (success) {
            mapOf("code" to 200, "message" to "同步成功")
        } else {
            mapOf("code" to 400, "message" to "同步失败")
        }
    }

    /**
     * 获取当前用户的所有行程（Android 端行程日志用）
     */
    @GetMapping("/my")
    fun getMyTrips(
        @RequestHeader("Authorization") authorization: String
    ): Map<String, Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return mapOf("code" to 401, "message" to "Token 无效或已过期")
        }

        val userId = jwtUtil.extractUserId(token)
        val trips = tripService.getTripsByUser(userId)

        return mapOf("code" to 200, "message" to "success", "data" to trips)
    }

    /**
     * 获取所有行程（管理员专用）
     */
    @GetMapping("/all")
    fun getAllTrips(
        @RequestHeader("Authorization") authorization: String
    ): Map<String, Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return mapOf("code" to 401, "message" to "Token 无效或已过期")
        }

        val userId = jwtUtil.extractUserId(token)

        // 权限检查：只有管理员（role = 1）可以查看所有行程
        val user = userService.getUserById(userId)
        if (user == null || user.role != 1) {
            return mapOf("code" to 403, "message" to "权限不足，仅管理员可访问")
        }

        val trips = tripService.getAllTrips()
        return mapOf("code" to 200, "message" to "success", "data" to trips)
    }

    /**
     * 获取平台统计（管理员专用）
     */
    @GetMapping("/statistics")
    fun getPlatformStatistics(
        @RequestHeader("Authorization") authorization: String
    ): Map<String, Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return mapOf("code" to 401, "message" to "Token 无效或已过期")
        }

        val userId = jwtUtil.extractUserId(token)

        val user = userService.getUserById(userId)
        if (user == null || user.role != 1) {
            return mapOf("code" to 403, "message" to "权限不足，仅管理员可访问")
        }

        val statistics = tripService.getPlatformStatistics()
        return mapOf("code" to 200, "message" to "success", "data" to statistics)
    }
}
