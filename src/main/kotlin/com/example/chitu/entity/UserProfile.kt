package com.example.chitu.entity

import com.baomidou.mybatisplus.annotation.*

@TableName("user_profile")
data class UserProfile(
    @TableId(value = "profile_id", type = IdType.AUTO)
    val profileId: Long? = null,

    @TableField("user_id")
    val userId: Long,

    @TableField("nickname")
    val nickname: String? = null,

    @TableField("avatar")
    val avatar: String? = null,

    @TableField("age")
    val age: Int? = null,

    @TableField("gender")
    val gender: Int = 0,

    @TableField("emergency_phone")
    val emergencyPhone: String? = null,

    @TableField("security_question")
    val securityQuestion: String? = null,

    @TableField("security_answer")
    val securityAnswer: String? = null
)