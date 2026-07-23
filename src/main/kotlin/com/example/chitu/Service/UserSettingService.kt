package com.example.chitu.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.chitu.dto.UserSettingResponse
import com.example.chitu.entity.UserSetting
import com.example.chitu.mapper.UserSettingMapper
import org.springframework.stereotype.Service

@Service
class UserSettingService(
    private val userSettingMapper: UserSettingMapper
) {

    /**
     * 获取用户设置
     * 如果数据库没有记录，返回默认值
     */
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
            // ✅ 默认值在 Service 层定义
            UserSettingResponse(
                darkMode = 0,
                soundEnabled = 1,
                vibrationEnabled = 1,
                reminderInterval = 240
            )
        }
    }
}