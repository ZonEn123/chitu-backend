package com.example.chitu.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.example.chitu.dto.LoginRequest
import com.example.chitu.dto.RegisterRequest
import com.example.chitu.dto.UpdateProfileRequest
import com.example.chitu.dto.UserProfileResponse
import com.example.chitu.entity.DriverStatistics
import com.example.chitu.entity.User
import com.example.chitu.entity.UserProfile
import com.example.chitu.entity.UserSetting
import com.example.chitu.mapper.DriverStatisticsMapper
import com.example.chitu.mapper.UserMapper
import com.example.chitu.mapper.UserProfileMapper
import com.example.chitu.mapper.UserSettingMapper
import com.example.chitu.utils.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userMapper: UserMapper,
    private val userProfileMapper: UserProfileMapper,
    private val userSettingMapper: UserSettingMapper,
    private val driverStatisticsMapper: DriverStatisticsMapper,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()


    private fun generateDefaultNickname(): String {
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val randomStr = (1..6).map { chars.random() }.joinToString("")
        return "吕小布$randomStr"
    }

    // 注册
    @Transactional
    fun register(request: RegisterRequest): User? {
        // 1. 检查手机号是否已存在
        val existing = userMapper.selectOne(QueryWrapper<User>().eq("phone", request.phone))
        if (existing != null) return null

        // 2. 加密密码
        val encryptedPassword = passwordEncoder.encode(request.password)

        // 3. 创建用户
        val user = User(
            phone = request.phone,
            password = encryptedPassword,
            role = 0,  // 默认普通司机
            status = 1
        )
        userMapper.insert(user)

        val finalNickname = request.nickname?.takeIf { it.isNotBlank() } ?: generateDefaultNickname()
        val profile = UserProfile(
            userId = user.userId!!,
            nickname = finalNickname,
            gender = 1   // 默认男
        )
        userProfileMapper.insert(profile)

        // 5. 创建关联的设置（默认值）
        val setting = UserSetting(userId = user.userId)
        userSettingMapper.insert(setting)

        // 6. 创建驾驶统计记录（默认0）
        val statistics = DriverStatistics(userId = user.userId)
        driverStatisticsMapper.insert(statistics)
        return user
    }

    // 登录
    fun login(request: LoginRequest): User? {
        // 1. 查询用户
        val user = userMapper.selectOne(QueryWrapper<User>().eq("phone", request.phone))
        if (user == null) return null

        // 2. 校验密码
        if (!passwordEncoder.matches(request.password, user.password)) return null

        // 3. 检查账号状态
        if (user.status == 0) return null

        // 4. 更新最后登录时间
        val now = LocalDateTime.now()
        val updateWrapper = UpdateWrapper<User>()
            .eq("user_id", user.userId)
            .set("last_login_time", now)
        userMapper.update(null, updateWrapper)

        // 返回更新后的用户信息（可选，返回原对象也行）

        return user
    }

    // 根据 userId 获取用户
    fun getUserById(userId: Long): User? {
        return userMapper.selectById(userId)
    }

    // 生成 Token
    fun generateToken(userId: Long): String {
        return jwtUtil.generateToken(userId)
    }

    // 验证 Token
    fun validateToken(token: String): Boolean {
        return jwtUtil.validateToken(token)
    }

    // 从 Token 提取用户 ID
    fun getUserIdFromToken(token: String): Long {
        return jwtUtil.extractUserId(token)
    }

    /**
     * 获取用户完整信息（含关联的 profile）
     * 只返回 user 表和 user_profile 表中实际存在的字段
     */
    fun getUserProfile(userId: Long): UserProfileResponse? {
        // 1. 查用户表
        val user = userMapper.selectById(userId) ?: return null

        // 2. 查用户资料表
        val profile = userProfileMapper.selectOne(
            QueryWrapper<UserProfile>().eq("user_id", userId)
        )

        // 3. 组装返回
        return UserProfileResponse(
            userId = user.userId!!,
            phone = user.phone,
            nickname = profile?.nickname,
            role = user.role,
            status = user.status,
            avatar = profile?.avatar,
            age = profile?.age,
            gender = profile?.gender ?: 0,
            emergencyPhone = profile?.emergencyPhone,
            securityQuestion = profile?.securityQuestion,
            securityAnswer = profile?.securityAnswer
        )
    }

    /**
     * 更新用户个人信息
     * 支持部分更新：只更新 request 中传入的字段
     */
    @Transactional
    fun updateUserProfile(userId: Long, request: UpdateProfileRequest): Boolean {
        val user = userMapper.selectById(userId) ?: return false

        val profile = userProfileMapper.selectOne(
            QueryWrapper<UserProfile>().eq("user_id", userId)
        ) ?: return false

        val updateWrapper = UpdateWrapper<UserProfile>()
            .eq("user_id", userId)

        // 昵称
        request.nickname?.takeIf { it.isNotBlank() }?.let {
            updateWrapper.set("nickname", it)
        }

        // 手机号
        request.phone?.takeIf { it.isNotBlank() }?.let { newPhone ->
            val existing = userMapper.selectOne(
                QueryWrapper<User>().eq("phone", newPhone).ne("user_id", userId)
            )
            if (existing != null) {
                throw IllegalArgumentException("手机号已被其他用户使用")
            }
            // phone 属于 user 表，只更新 user，不写入 user_profile
            val userUpdate = UpdateWrapper<User>()
                .eq("user_id", userId)
                .set("phone", newPhone)
            userMapper.update(null, userUpdate)
        }

        // 年龄
        request.age?.takeIf { it in 1..120 }?.let {
            updateWrapper.set("age", it)
        }

        // 性别
        request.gender?.takeIf { it in 0..1 }?.let {
            updateWrapper.set("gender", it)
        }

        // 紧急联系人
        request.emergencyPhone?.takeIf { it.isNotBlank() }?.let {
            updateWrapper.set("emergency_phone", it)
        }

        // 密保：只有首次设置时才允许写入
        val currentProfile = userProfileMapper.selectOne(
            QueryWrapper<UserProfile>().eq("user_id", userId)
        )

        val isSecurityAlreadySet = !currentProfile?.securityQuestion.isNullOrEmpty() &&
                !currentProfile?.securityAnswer.isNullOrEmpty()

        if (!isSecurityAlreadySet) {
            request.securityQuestion?.takeIf { it.isNotBlank() }?.let {
                updateWrapper.set("security_question", it)
            }
            request.securityAnswer?.takeIf { it.isNotBlank() }?.let {
                updateWrapper.set("security_answer", it)
            }
        }

        if (updateWrapper.sqlSet.isEmpty()) {
            return true
        }

        val result = userProfileMapper.update(null, updateWrapper)
        return result > 0
    }
}