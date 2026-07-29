package com.example.chitu.config

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): Map<String, Any> {
        return mapOf(
            "code" to 500,
            "message" to ("服务器内部错误: " + (e.message ?: "未知错误"))
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): Map<String, Any> {
        val msg: String = e.message ?: "请求参数错误"
        return mapOf(
            "code" to 400,
            "message" to msg
        )
    }
}
