package com.example.chitu.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class TripVO(
    val tripId: Long,
    val userId: Long,
    val phone: String,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val duration: Int,
    val startLocation: String?,
    val endLocation: String?,
    val distance: BigDecimal,
    val tripStatus: Int,
    val fatigueFlag: Int
)
