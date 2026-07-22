package com.example.chitu.dto

data class LoginResponse(
    val token: String,
    val userId: Long,
    val phone: String,
    val nickname: String? = null,
    val role: Int
)