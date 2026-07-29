package com.example.chitu.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.chitu.entity.DriverStatistics
import com.example.chitu.entity.TripLog
import com.example.chitu.mapper.DriverStatisticsMapper
import com.example.chitu.mapper.TripLogMapper
import com.example.chitu.mapper.UserMapper
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DriverStatisticsService(
    private val driverStatisticsMapper: DriverStatisticsMapper,
    private val tripLogMapper: TripLogMapper,
    private val userMapper: UserMapper
) {

    /** 获取所有司机的统计列表 */
    fun getAllDriverStatistics(): List<Map<String, Any>> {
        val trips = tripLogMapper.selectList(
            QueryWrapper<TripLog>().eq("trip_status", 1)
        )
        if (trips.isEmpty()) return emptyList()

        val grouped = trips.groupBy { it.userId }
        val userIds = grouped.keys
        val users = userMapper.selectBatchIds(userIds).associateBy { (it as com.example.chitu.entity.User).userId }

        return grouped.map { (userId, userTrips) ->
            val user = users[userId]
            mapOf(
                "userId" to userId,
                "phone" to (user?.phone ?: "未知"),
                "totalDurationSeconds" to userTrips.sumOf { it.duration ?: 0 },
                "totalDistanceKm" to userTrips.sumOf { it.distance?.toDouble() ?: 0.0 } / 1000,
                "totalTripCount" to userTrips.size,
                "fatigueCount" to userTrips.count { it.fatigueFlag == 1 }
            )
        }
    }

    /** 获取平台总体统计 */
    fun getPlatformStats(): Map<String, Any> {
        val trips = tripLogMapper.selectList(
            QueryWrapper<TripLog>().eq("trip_status", 1)
        )
        val totalDuration = trips.sumOf { it.duration ?: 0 }
        val totalDistance = trips.sumOf { it.distance?.toDouble() ?: 0.0 } / 1000
        val totalTrips = trips.size
        val totalFatigue = trips.count { it.fatigueFlag == 1 }
        val totalDrivers = trips.map { it.userId }.distinct().size

        return mapOf(
            "totalDurationHours" to String.format("%.1f", totalDuration / 3600.0).toDouble(),
            "totalDistanceKm" to String.format("%.1f", totalDistance).toDouble(),
            "totalTrips" to totalTrips,
            "totalFatigue" to totalFatigue,
            "totalDrivers" to totalDrivers
        )
    }
}
