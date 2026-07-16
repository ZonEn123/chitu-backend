package com.example.chitu.entity

import com.baomidou.mybatisplus.annotation.*

@TableName("user_setting")
data class UserSetting(
    @TableId(value = "setting_id", type = IdType.AUTO)
    val settingId: Long? = null,

    @TableField("user_id")
    val userId: Long,

    @TableField("dark_mode")
    val darkMode: Int = 0,

    @TableField("sound_enabled")
    val soundEnabled: Int = 1,

    @TableField("vibration_enabled")
    val vibrationEnabled: Int = 1,

    @TableField("reminder_interval")
    val reminderInterval: Int = 240
)