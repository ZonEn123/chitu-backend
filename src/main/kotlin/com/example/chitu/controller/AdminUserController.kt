package com.example.chitu.controller

import com.example.chitu.service.AdminUserService
import com.example.chitu.service.UserService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    /** 查询用户列表（仅管理员） */
    @GetMapping
    fun getUserList(
        @RequestHeader("Authorization") authorization: String
    ): Map<String, Any> {
        val check = checkAdmin(authorization) ?: return error(403, "权限不足")
        val users = adminUserService.getUserList()
        return mapOf("code" to 200, "message" to "success", "data" to users)
    }

    /** 查询用户资料（仅管理员） */
    @GetMapping("/{userId}/profile")
    fun getUserProfile(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable userId: Long
    ): Map<String, Any> {
        val check = checkAdmin(authorization) ?: return error(403, "权限不足")
        val profile = adminUserService.getUserProfile(userId)
        return if (profile != null) {
            mapOf("code" to 200, "message" to "success", "data" to profile)
        } else {
            mapOf("code" to 404, "message" to "用户资料不存在")
        }
    }

    /** 修改用户状态（封禁/解封） */
    @PutMapping("/{id}/status")
    fun updateUserStatus(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable id: Long,
        @RequestBody body: Map<String, Any>
    ): Map<String, Any> {
        val check = checkAdmin(authorization) ?: return error(403, "权限不足")
        val status = (body["status"] as? Int) ?: return mapOf("code" to 400, "message" to "参数缺失")
        if (status !in listOf(0, 1)) return mapOf("code" to 400, "message" to "无效的状态值")
        return if (adminUserService.updateStatus(id, status)) {
            mapOf("code" to 200, "message" to "操作成功")
        } else {
            mapOf("code" to 500, "message" to "操作失败")
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

    private fun error(code: Int, msg: String) = mapOf("code" to code, "message" to msg)
}
