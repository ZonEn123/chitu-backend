package com.example.chitu.dto


/**
 * 管理员查看用户资料
 *
 * 对应 user_profile 表
 */
data class AdminUserProfileResponse(

    /**
     * 昵称
     */
    val nickname: String?,


    /**
     * 头像
     */
    val avatar: String?,


    /**
     * 年龄
     */
    val age: Int?,


    /**
     * 性别
     *
     * 0 女
     * 1 男
     */
    val gender: Int,


    /**
     * 紧急联系人电话
     */
    val emergencyPhone: String?

)