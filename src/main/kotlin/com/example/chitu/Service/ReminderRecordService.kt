package com.example.chitu.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.chitu.entity.ReminderRecord
import com.example.chitu.mapper.ReminderRecordMapper
import org.springframework.stereotype.Service

@Service
class ReminderRecordService(
    private val reminderRecordMapper: ReminderRecordMapper
) {

    /** 获取所有提醒记录（含用户信息） */
    fun getAllReminders(): List<ReminderRecord> {
        return reminderRecordMapper.selectList(
            QueryWrapper<ReminderRecord>().orderByDesc("reminder_time")
        )
    }

    /** 获取某个用户的提醒记录 */
    fun getRemindersByUser(userId: Long): List<ReminderRecord> {
        return reminderRecordMapper.selectList(
            QueryWrapper<ReminderRecord>()
                .eq("user_id", userId)
                .orderByDesc("reminder_time")
        )
    }

    /** 获取某个行程的提醒记录 */
    fun getRemindersByTrip(tripId: Long): List<ReminderRecord> {
        return reminderRecordMapper.selectList(
            QueryWrapper<ReminderRecord>()
                .eq("trip_id", tripId)
                .orderByDesc("reminder_time")
        )
    }

    /** 保存提醒记录 */
    fun save(record: ReminderRecord): Boolean {
        return reminderRecordMapper.insert(record) > 0
    }
}
