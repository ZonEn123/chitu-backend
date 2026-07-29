package com.example.chitu.controller

import com.example.chitu.dto.ApiResponse
import com.example.chitu.entity.ReminderRecord
import com.example.chitu.service.ReminderRecordService
import com.example.chitu.service.UserService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
class ReminderRecordController(
    private val reminderRecordService: ReminderRecordService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/admin/reminders")
    fun getAllReminders(
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) return ApiResponse.error(401, "Token 无效")
        val user = userService.getUserById(jwtUtil.extractUserId(token))
        if (user == null || user.role != 1) return ApiResponse.error(403, "权限不足")
        return ApiResponse.success(reminderRecordService.getAllReminders())
    }

    @GetMapping("/admin/reminders/user/{userId}")
    fun getRemindersByUser(
        @PathVariable userId: Long,
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) return ApiResponse.error(401, "Token 无效")
        return ApiResponse.success(reminderRecordService.getRemindersByUser(userId))
    }

    @PostMapping("/reminders")
    fun saveReminder(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody body: Map<String, Any>
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) return ApiResponse.error(401, "Token 无效")
        val userId = jwtUtil.extractUserId(token)

        val record = ReminderRecord(
            userId = userId,
            reminderTime = LocalDateTime.now(),
            reminderType = (body["reminderType"] as? String) ?: "",
            tripId = (body["tripId"] as? Number)?.toLong()
        )
        reminderRecordService.save(record)
        return ApiResponse.success(message = "保存成功")
    }
}
