package com.example.chitu.controller

import com.example.chitu.dto.ApiResponse
import com.example.chitu.service.UserService
import com.example.chitu.utils.JwtUtil
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserPasswordController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/security-question")
    fun getSecurityQuestion(
        @RequestHeader("Authorization") authorization: String
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) return ApiResponse.error(401, "Token 无效")
        val userId = jwtUtil.extractUserId(token)
        val question = userService.getSecurityQuestion(userId)
        if (question.isNullOrBlank()) {
            return ApiResponse.error(400, "未设置密保问题")
        }
        return ApiResponse.success(mapOf("question" to question))
    }

    @PutMapping("/password")
    fun changePassword(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody body: Map<String, String>
    ): ApiResponse<Any> {
        val token = authorization.replace("Bearer ", "")
        if (!jwtUtil.validateToken(token)) return ApiResponse.error(401, "Token 无效")
        val userId = jwtUtil.extractUserId(token)
        val answer = body["answer"] ?: return ApiResponse.error(400, "密保答案不能为空")
        val newPwd = body["newPassword"] ?: return ApiResponse.error(400, "新密码不能为空")

        if (!userService.verifySecurityAnswer(userId, answer)) {
            return ApiResponse.error(400, "密保答案错误")
        }
        userService.updatePassword(userId, newPwd)
        return ApiResponse.success(message = "密码修改成功")
    }
}
