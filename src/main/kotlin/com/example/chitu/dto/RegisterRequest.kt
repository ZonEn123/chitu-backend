package com.example.chitu.dto

data class RegisterRequest(
    val phone: String,
    val password: String,
    val nickname: String? = null
)