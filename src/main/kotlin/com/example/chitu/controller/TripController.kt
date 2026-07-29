package com.example.chitu.controller

import com.example.chitu.dto.ApiResponse
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

    @PostMapping("/sync")
    fun syncTrip(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: TripSyncRequest
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val userId = jwtUtil.extractUserId(token)
        val success = tripService.syncTrip(userId, request.clientId, request)
        return if (success) {
            ApiResponse.success(message = "同步成功")
        } else {
            ApiResponse.error(400, "同步失败")
        }
    }

    @GetMapping("/my")
    fun getMyTrips(
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val userId = jwtUtil.extractUserId(token)
        val trips = tripService.getTripsByUser(userId)
        return ApiResponse.success(trips)
    }

    @GetMapping("/all")
    fun getAllTrips(
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val user = userService.getUserById(jwtUtil.extractUserId(token))
        if (user == null || user.role != 1) {
            return ApiResponse.error(403, "权限不足，仅管理员可访问")
        }
        return ApiResponse.success(tripService.getAllTrips())
    }

    @GetMapping("/statistics")
    fun getPlatformStatistics(
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "Token 无效或已过期")
        }
        val user = userService.getUserById(jwtUtil.extractUserId(token))
        if (user == null || user.role != 1) {
            return ApiResponse.error(403, "权限不足，仅管理员可访问")
        }
        return ApiResponse.success(tripService.getPlatformStatistics())
    }
}
