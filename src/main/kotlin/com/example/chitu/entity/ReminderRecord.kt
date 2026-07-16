package com.example.chitu.entity

import com.baomidou.mybatisplus.annotation.*
import java.time.LocalDateTime

@TableName("reminder_record")
data class ReminderRecord(
    @TableId(value = "reminder_id", type = IdType.AUTO)
    val reminderId: Long? = null,

    @TableField("user_id")
    val userId: Long,

    @TableField("trip_id")
    val tripId: Long? = null,

    @TableField("reminder_type")
    val reminderType: String? = null,

    @TableField("reminder_time")
    val reminderTime: LocalDateTime? = null,

    @TableField("is_confirmed")
    val isConfirmed: Int = 0
)