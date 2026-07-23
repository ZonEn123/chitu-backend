package com.example.chitu.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.chitu.dto.TripSyncRequest
import com.example.chitu.entity.TripLog
import com.example.chitu.mapper.TripLogMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class TripService(
    private val tripLogMapper: TripLogMapper
) {

    /**
     * 同步单条行程（幂等）
     * @param userId 用户ID
     * @param clientId 客户端唯一ID
     * @param request 行程数据
     * @return true-插入成功 false-已存在（幂等）
     */
    @Transactional
    fun syncTrip(
        userId: Long,
        clientId: String,
        request: TripSyncRequest
    ): Boolean {
        // 幂等检查：根据 clientId 查询是否已存在
        val existing = tripLogMapper.selectOne(
            QueryWrapper<TripLog>().eq("client_id", clientId)
        )

        if (existing != null) {
            return true
        }

        // 不存在，插入新记录
        val trip = TripLog(
            userId = userId,
            clientId = clientId,
            startTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(request.startTime),
                ZoneId.systemDefault()
            ),
            endTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(request.endTime),
                ZoneId.systemDefault()
            ),
            duration = request.durationSeconds,
            startLocation = request.startLocation,
            endLocation = request.endLocation,
            distance = BigDecimal.valueOf(request.distanceMeters.toDouble()),
            tripStatus = request.tripStatus,
            fatigueFlag = request.fatigueFlag,
            remark = request.remark,
            syncStatus = 1,
            startLatitude = request.startLatitude,
            startLongitude = request.startLongitude,
            endLatitude = request.endLatitude,
            endLongitude = request.endLongitude
        )

        return tripLogMapper.insert(trip) > 0
    }

    /** 获取用户所有行程 */
    fun getTripsByUser(userId: Long): List<TripLog> {
        return tripLogMapper.selectList(
            QueryWrapper<TripLog>()
                .eq("user_id", userId)
                .orderByDesc("start_time")
        )
    }

    /** 获取所有行程（管理员专用） */
    fun getAllTrips(): List<TripLog> {
        return tripLogMapper.selectList(
            QueryWrapper<TripLog>()
                .orderByDesc("start_time")
        )
    }

    /** 获取平台统计（管理员专用） */
    fun getPlatformStatistics(): Map<String, Any> {
        val allTrips = tripLogMapper.selectList(null)
        var totalDuration = 0
        var totalDistance = 0.0
        var totalTrips = allTrips.size
        var totalFatigue = 0

        allTrips.forEach { trip ->
            totalDuration += trip.duration ?: 0
            totalDistance += trip.distance?.toDouble() ?: 0.0
            if (trip.fatigueFlag == 1) totalFatigue++
        }

        return mapOf(
            "totalDurationSeconds" to totalDuration,
            "totalDistanceKm" to (totalDistance / 1000).let {
                String.format("%.2f", it).toDouble()
            },
            "totalTrips" to totalTrips,
            "totalFatigue" to totalFatigue
        )
    }
}
