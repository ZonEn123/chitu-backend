package com.example.chitu.entity

import com.baomidou.mybatisplus.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@TableName("trip_log")
data class TripLog(
    @TableId(value = "trip_id", type = IdType.AUTO)
    val tripId: Long? = null,

    @TableField("user_id")
    val userId: Long,

    @TableField("start_time")
    val startTime: LocalDateTime,

    @TableField("end_time")
    val endTime: LocalDateTime? = null,

    @TableField("duration")
    val duration: Int = 0,

    @TableField("start_location")
    val startLocation: String? = null,

    @TableField("end_location")
    val endLocation: String? = null,

    @TableField("distance")
    val distance: BigDecimal = BigDecimal.ZERO,

    @TableField("remark")
    val remark: String? = null,

    @TableField("trip_status")
    val tripStatus: Int = 0,  // 0-进行中 1-已完成 2-异常结束

    @TableField("fatigue_flag")
    val fatigueFlag: Int = 0  // 0-否 1-是
)