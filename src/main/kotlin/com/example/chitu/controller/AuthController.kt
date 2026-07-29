package com.example.chitu.controller

import com.example.chitu.dto.ApiResponse
import com.example.chitu.dto.LoginRequest
import com.example.chitu.dto.LoginResponse
import com.example.chitu.dto.RegisterRequest
import com.example.chitu.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ApiResponse<Any> {
        val existing = userService.getUserByPhone(request.phone)
        if (existing != null) {
            return ApiResponse.error(409, "手机号已注册")
        }
        val user = userService.register(request)
        return if (user != null) {
            ApiResponse.success(mapOf("userId" to user.userId), "注册成功")
        } else {
            ApiResponse.error(500, "注册失败")
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        // 1. 检查手机号
        val user = userService.getUserByPhone(request.phone)
        if (user == null) {
            return ApiResponse.error(401, "手机号未注册")
        }
        // 2. 检查封禁
        if (user.status == 0) {
            return ApiResponse.error(403, "账号已被封禁，请联系管理员")
        }
        // 3. 校验密码
        val loginUser = userService.login(request)
        if (loginUser != null) {
            val token = userService.generateToken(loginUser.userId!!)
            return ApiResponse.success(
                LoginResponse(token, loginUser.userId!!, loginUser.phone, null, loginUser.role)
            )
        }
        return ApiResponse.error(401, "密码错误")
    }

    // ========== 安全模块 ==========

    @GetMapping("/security-question")
    fun getSecurityQuestion(@RequestParam phone: String): ApiResponse<Any> {
        val user = userService.getUserByPhone(phone) ?: return ApiResponse.error(404, "手机号未注册")
        val question = userService.getSecurityQuestion(user.userId!!)
        if (question.isNullOrBlank()) {
            return ApiResponse.error(400, "该用户未设置密保")
        }
        return ApiResponse.success(mapOf("question" to question))
    }

    @PostMapping("/verify-security")
    fun verifySecurity(@RequestBody body: Map<String, String>): ApiResponse<Any> {
        val phone = body["phone"] ?: return ApiResponse.error(400, "手机号不能为空")
        val answer = body["answer"] ?: return ApiResponse.error(400, "答案不能为空")
        val user = userService.getUserByPhone(phone) ?: return ApiResponse.error(404, "手机号未注册")
        return if (userService.verifySecurityAnswer(user.userId!!, answer)) {
            ApiResponse.success(message = "验证成功")
        } else {
            ApiResponse.error(400, "答案错误")
        }
    }

    @PutMapping("/reset-password")
    fun resetPassword(@RequestBody body: Map<String, String>): ApiResponse<Any> {
        val phone = body["phone"] ?: return ApiResponse.error(400, "手机号不能为空")
        val newPwd = body["newPassword"] ?: return ApiResponse.error(400, "新密码不能为空")
        val user = userService.getUserByPhone(phone) ?: return ApiResponse.error(404, "手机号未注册")
        userService.updatePasswordByPhone(phone, newPwd)
        return ApiResponse.success(message = "密码修改成功")
    }
}
