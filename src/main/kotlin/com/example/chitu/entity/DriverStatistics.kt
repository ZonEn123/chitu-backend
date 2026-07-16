package com.example.chitu.entity

import com.baomidou.mybatisplus.annotation.*
import java.math.BigDecimal

@TableName("driver_statistics")
data class DriverStatistics(
    @TableId(value = "statistics_id", type = IdType.AUTO)
    val statisticsId: Long? = null,

    @TableField("user_id")
    val userId: Long,

    @TableField("total_duration")
    val totalDuration: Int = 0,

    @TableField("total_distance")
    val totalDistance: BigDecimal = BigDecimal.ZERO,

    @TableField("total_trip_count")
    val totalTripCount: Int = 0,

    @TableField("fatigue_count")
    val fatigueCount: Int = 0
)