package com.example.chitu.dto

data class UpdateSettingRequest(
    val darkMode: Int? = null,          // 0-浅色 1-深色
    val soundEnabled: Int? = null,      // 0-关闭 1-开启
    val vibrationEnabled: Int? = null,  // 0-关闭 1-开启
    val reminderInterval: Int? = null   // 疲劳提醒间隔（分钟）
)