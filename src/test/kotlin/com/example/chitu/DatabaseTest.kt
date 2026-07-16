package com.example.chitu

import com.example.chitu.mapper.UserMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DatabaseTest {

    @Autowired
    private lateinit var userMapper: UserMapper

    @Test
    fun testConnection() {
        // 查询所有用户（刚开始只有 admin 一条数据）
        val users = userMapper.selectList(null)
        println("✅ 数据库连接成功！当前用户数：${users.size}")
        users.forEach {
            println("📱 用户：${it.phone}，角色：${if (it.role == 1) "管理员" else "普通司机"}")
        }
    }
}