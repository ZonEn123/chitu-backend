package com.example.chitu.dto

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T? = null, message: String = "success"): ApiResponse<T> =
            ApiResponse(200, message, data)

        fun <T> error(code: Int = 400, message: String): ApiResponse<T> =
            ApiResponse(code, message, null)
    }
}
