package com.example.chitu.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.example.chitu.dto.UpdateSettingRequest
import com.example.chitu.dto.UserSettingResponse
import com.example.chitu.entity.UserSetting
import com.example.chitu.mapper.UserSettingMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSettingService(
    private val userSettingMapper: UserSettingMapper
) {

    fun getUserSetting(userId: Long): UserSettingResponse {
        val setting = userSettingMapper.selectOne(
            QueryWrapper<UserSetting>().eq("user_id", userId)
        )

        return if (setting != null) {
            UserSettingResponse(
                darkMode = setting.darkMode,
                soundEnabled = setting.soundEnabled,
                vibrationEnabled = setting.vibrationEnabled,
                reminderInterval = setting.reminderInterval
            )
        } else {
            UserSettingResponse(
                darkMode = 0,
                soundEnabled = 1,
                vibrationEnabled = 1,
                reminderInterval = 240
            )
        }
    }

    // ✅ 新增：保存设置
    @Transactional
    fun updateUserSetting(userId: Long, request: UpdateSettingRequest): Boolean {
        // 1. 检查是否存在记录
        val existing = userSettingMapper.selectOne(
            QueryWrapper<UserSetting>().eq("user_id", userId)
        )

        if (existing == null) {
            // 如果不存在，创建一条默认记录
            val newSetting = UserSetting(
                userId = userId,
                darkMode = request.darkMode ?: 0,
                soundEnabled = request.soundEnabled ?: 1,
                vibrationEnabled = request.vibrationEnabled ?: 1,
                reminderInterval = request.reminderInterval ?: 240
            )
            return userSettingMapper.insert(newSetting) > 0
        }

        // 2. 存在则更新
        val updateWrapper = UpdateWrapper<UserSetting>()
            .eq("user_id", userId)

        request.darkMode?.let { updateWrapper.set("dark_mode", it) }
        request.soundEnabled?.let { updateWrapper.set("sound_enabled", it) }
        request.vibrationEnabled?.let { updateWrapper.set("vibration_enabled", it) }
        request.reminderInterval?.let { updateWrapper.set("reminder_interval", it) }

        // 如果没有任何字段需要更新，直接返回 true
        if (updateWrapper.sqlSet.isEmpty()) {
            return true
        }

        return userSettingMapper.update(null, updateWrapper) > 0
    }
}