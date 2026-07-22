package com.example.chitu.dto

/**
 * 用户个人信息响应 DTO
 * 字段必须与数据库 user 表和 user_profile 表的实际字段完全一致
 */
data class UserProfileResponse(
    val userId: Long,
    val phone: String,          // 来自 user 表
    val nickname: String?,      // 来自 user_profile 表
    val role: Int,              // 来自 user 表
    val status: Int,            // 来自 user 表
    val avatar: String?,        // 来自 user_profile 表
    val age: Int?,              // 来自 user_profile 表
    val gender: Int,            // 来自 user_profile 表
    val emergencyPhone: String?,// 来自 user_profile 表
    val securityQuestion: String?, // 来自 user_profile 表（可选）
    val securityAnswer: String?    // 来自 user_profile 表（可选）
)