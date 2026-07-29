package com.example.chitu.dto

import java.time.LocalDateTime


/**
 * 管理员用户列表返回对象
 *
 * 对应 user 表
 */
data class AdminUserListResponse(

    /**
     * 用户ID
     */
    val userId: Long,


    /**
     * 手机号
     */
    val phone: String,


    /**
     * 用户角色
     *
     * 0 普通司机
     * 1 管理员
     */
    val role: Int,


    /**
     * 用户状态
     *
     * 0 禁用
     * 1 正常
     */
    val status: Int,


    /**
     * 注册时间
     */
    val registerTime: LocalDateTime?

)