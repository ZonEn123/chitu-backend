package com.example.chitu.controller

import com.example.chitu.service.TripService
import com.example.chitu.service.UserService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/trips")
class AdminTripController(
    private val tripService: TripService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping
    fun getAllTrips(
        @RequestHeader("Authorization") authorization: String
    ): Map<String, Any> {
        val check = checkAdmin(authorization) ?: return mapOf("code" to 403, "message" to "权限不足")
        return try {
            val trips = tripService.getAllTripsWithUser()
            mapOf("code" to 200, "data" to trips)
        } catch (e: Exception) {
            mapOf("code" to 500, "message" to "获取行程列表失败: ${e.message}")
        }
    }

    /** 逻辑删除行程 */
    @DeleteMapping("/{tripId}")
    fun deleteTrip(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable tripId: Long
    ): Map<String, Any> {
        val check = checkAdmin(authorization) ?: return mapOf("code" to 403, "message" to "权限不足")
        return try {
            val ok = tripService.deleteTrip(tripId)
            if (ok) mapOf("code" to 200, "message" to "删除成功")
            else mapOf("code" to 404, "message" to "行程不存在")
        } catch (e: Exception) {
            mapOf("code" to 500, "message" to "删除失败: ${e.message}")
        }
    }

    private fun checkAdmin(auth: String): Boolean? {
        return try {
            val token = auth.replace("Bearer ", "")
            if (!jwtUtil.validateToken(token)) return false
            val userId = jwtUtil.extractUserId(token)
            val user = userService.getUserById(userId)
            if (user == null || user.role != 1) false else true
        } catch (_: Exception) { false }
    }
}
