package com.example.chitu.service


import com.example.chitu.dto.AdminUserListResponse
import com.example.chitu.dto.AdminUserProfileResponse
import com.example.chitu.mapper.UserMapper
import com.example.chitu.mapper.UserProfileMapper
import org.springframework.stereotype.Service
import com.example.chitu.entity.UserProfile

@Service
class AdminUserService(

    private val userMapper: UserMapper,

    private val userProfileMapper: UserProfileMapper

) {


    /**
     * 查询所有用户
     *
     * 数据来源：
     * user表
     */
    fun getUserList(): List<AdminUserListResponse> {


        val users = userMapper.selectList(null)


        return users.map {


            AdminUserListResponse(

                userId = it.userId!!,

                phone = it.phone,

                role = it.role,

                status = it.status,

                registerTime = it.registerTime

            )

        }

    }



    /**
     * 查询用户详细资料
     *
     * 数据来源：
     * user_profile表
     */
    fun getUserProfile(
        userId: Long
    ): AdminUserProfileResponse? {


        val profile =
            userProfileMapper.selectOne(

                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserProfile>()

                    .eq(
                        "user_id",
                        userId
                    )
            )


        return profile?.let {


            AdminUserProfileResponse(

                nickname = it.nickname,

                avatar = it.avatar,

                age = it.age,

                gender = it.gender,

                emergencyPhone = it.emergencyPhone

            )


        }

    }

    /** 修改用户状态（封禁/解封） */
    fun updateStatus(userId: Long, status: Int): Boolean {
        return userMapper.update(
            null,
            com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<com.example.chitu.entity.User>()
                .eq("user_id", userId)
                .set("status", status)
        ) > 0
    }
}
