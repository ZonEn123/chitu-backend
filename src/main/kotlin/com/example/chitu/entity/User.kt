package com.example.chitu.entity

import com.baomidou.mybatisplus.annotation.*
import java.time.LocalDateTime

@TableName("user")
data class User(
    @TableId(value = "user_id", type = IdType.AUTO)
    val userId: Long? = null,

    @TableField("phone")
    val phone: String,

    @TableField("password")
    val password: String,

    @TableField("role")
    val role: Int = 0,  // 0-普通司机 1-管理员

    @TableField(value = "register_time", fill = FieldFill.INSERT)
    val registerTime: LocalDateTime? = null,

    @TableField("last_login_time")
    val lastLoginTime: LocalDateTime? = null,

    @TableField("status")
    val status: Int = 1  // 0-禁用 1-正常
)