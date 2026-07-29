<p align="center">
  <h1 align="center">🐎 赤兔 · Chitu Backend</h1>
  <p align="center">
    智能驾驶辅助系统 · Spring Boot 后端服务
  </p>
  <p align="center">
    <a href="#"><img src="https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen" /></a>
    <a href="#"><img src="https://img.shields.io/badge/Kotlin-2.0.21-purple" /></a>
    <a href="#"><img src="https://img.shields.io/badge/MyBatis--Plus-3.5.5-red" /></a>
    <a href="#"><img src="https://img.shields.io/badge/MySQL-8.0-blue" /></a>
    <a href="#"><img src="https://img.shields.io/badge/JWT-HS256-orange" /></a>
  </p>
</p>

---

## 📋 项目简介

**赤兔（Chitu）** 面向长途货车司机的智能驾驶辅助系统后端服务。为 Android 客户端和管理后台提供 RESTful API，负责用户认证、行程数据存储、驾驶统计、疲劳提醒记录等业务逻辑。

---

## 🏗️ 系统架构

```
┌───────────────────────────────────────────────────────────┐
│                    HTTP Request / Response                  │
│              Authorization: Bearer <JWT Token>              │
└─────────────────────────┬─────────────────────────────────┘
                          │
┌─────────────────────────▼─────────────────────────────────┐
│                    CONTROLLER LAYER                         │
│  ┌──────────┬──────────┬────────┬──────────┬────────────┐  │
│  │  Auth    │  User    │  Trip  │  Admin   │  Reminder  │  │
│  │  Ctrl    │  Ctrl    │  Ctrl  │  Ctrl    │  Ctrl      │  │
│  ├──────────┼──────────┼────────┼──────────┼────────────┤  │
│  │  Admin   │  Admin   │  User  │  Driver  │  User      │  │
│  │  UserCtrl│  TripCtrl│  Pwd   │  Stats   │  Setting   │  │
│  └──────────┴──────────┴────────┴──────────┴────────────┘  │
│         ↑ 参数校验 · JWT 认证 · 角色鉴权 (role=1)          │
├───────────────────────────────────────────────────────────┤
│                     SERVICE LAYER                            │
│  ┌────────────┬────────────┬──────────┬────────────────┐   │
│  │ UserService│ TripService│ AdminUser│ DriverStats    │   │
│  │            │            │ Service  │ Service        │   │
│  ├────────────┼────────────┼──────────┼────────────────┤   │
│  │ UserSetting│ Reminder   │ —        │ —              │   │
│  │ Service    │ Service    │          │                │   │
│  └────────────┴────────────┴──────────┴────────────────┘   │
│                  @Transactional 事务管理                      │
├───────────────────────────────────────────────────────────┤
│                     MAPPER LAYER                             │
│         6 × BaseMapper〈Entity〉 + QueryWrapper              │
├───────────────────────────────────────────────────────────┤
│                        MySQL 8.0                             │
│               chitu · 6 tables · utf8mb4                    │
└───────────────────────────────────────────────────────────┘
```

---

## 🛠 技术栈

| 组件 | 技术 | 版本 |
|:-----|:-----|:-----|
| 框架 | Spring Boot | 3.4.4 |
| 语言 | Kotlin | 2.0.21 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 认证 | jjwt (HMAC-SHA256) | 0.11.5 |
| 密码加密 | BCrypt (spring-security-crypto) | 6.3.0 |
| API 文档 | Springdoc OpenAPI | — |
| 构建工具 | Gradle | 9.5+ |

---

## 📡 API 总览（28 个端点）

### 公开接口（无认证）

| 方法 | 路径 | 说明 |
|:-----|:-----|:------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录，返回 JWT |
| GET | `/api/auth/security-question` | 查询密保问题 |
| POST | `/api/auth/verify-security` | 验证密保答案 |
| PUT | `/api/auth/reset-password` | 重置密码 |
| GET | `/api/test` | 健康检查 |

### JWT 认证接口（Android 端使用）

| 方法 | 路径 | 说明 |
|:-----|:-----|:------|
| GET | `/api/user/profile` | 获取个人信息 |
| PUT | `/api/user/profile` | 更新个人信息 |
| GET | `/api/user/setting` | 获取用户设置 |
| PUT | `/api/user/setting` | 更新用户设置 |
| GET | `/api/user/security-question` | 获取当前用户密保问题 |
| PUT | `/api/user/password` | 修改登录密码 |
| POST | `/api/trips/sync` | 同步行程（幂等） |
| GET | `/api/trips/my` | 获取当前用户行程 |
| GET | `/api/trips/all` | 全部行程（管理员） |
| GET | `/api/trips/statistics` | 平台统计（管理员） |
| POST | `/api/reminders` | 保存疲劳提醒记录 |

### 管理员接口（需验证 role=1）

| 方法 | 路径 | 说明 |
|:-----|:-----|:------|
| GET | `/api/admin/users` | 用户列表 |
| GET | `/api/admin/users/{id}/profile` | 用户资料详情 |
| PUT | `/api/admin/users/{id}/status` | 封禁/解封用户 |
| GET | `/api/admin/trips` | 全部行程列表 |
| DELETE | `/api/admin/trips/{id}` | 逻辑删除行程 |
| GET | `/api/admin/statistics/overview` | 平台统计总览 |
| GET | `/api/admin/statistics/drivers` | 司机驾驶排名 |
| GET | `/api/admin/reminders` | 疲劳提醒记录列表 |
| GET | `/api/admin/reminders/user/{id}` | 指定用户提醒记录 |

---

## 🗄️ 数据库设计

### 关系图

```
user ──1:1── user_profile
  │
  ├──1:1── user_setting
  │
  ├──1:1── driver_statistics
  │
  ├──1:N── trip_log
  │
  └──1:N── reminder_record

trip_log ──1:N── reminder_record
```

### 表结构

| 表 | 字段数 | 核心字段 | 职责 |
|:---|:------:|:---------|:-----|
| `user` | 7 | user_id, phone, password(BCrypt), role, status | 用户账号与认证 |
| `user_profile` | 9 | profile_id, user_id, nickname, gender, emergency_phone, security Q&A | 个人资料与密保 |
| `user_setting` | 5 | setting_id, user_id, dark_mode, sound_enabled, vibration_enabled, reminder_interval | 系统偏好配置 |
| `trip_log` | 18 | trip_id, user_id, client_id, start/end_time, location, distance, sync_status, deleted | 核心行程记录 |
| `driver_statistics` | 5 | statistics_id, user_id, total_duration, total_distance, total_trips, fatigue_count | 驾驶数据汇总 |
| `reminder_record` | 6 | reminder_id, user_id, trip_id, reminder_type, reminder_time, is_confirmed | 疲劳提醒历史 |

---

## 🔐 安全设计

### JWT 认证流程

```
登录 → 验证密码(BCrypt) → 生成JWT(24h, HS256) → 返回客户端
                                           ↓
后续请求 → Authorization: Bearer <token> → 解析验证 → 提取userId
                                                      ↓
管理员接口验证 role == 1，非管理员返回 403
```

### 数据权限

- 普通用户：只能访问自己的数据（`/api/trips/my` 绑定 JWT userId）
- 管理员：可访问全部数据（`/api/admin/*` 需 role=1）

### 密码安全

- BCryptPasswordEncoder 加密存储
- 每次 `encode()` 使用随机 Salt
- 同一密码每次加密结果不同

---

## 🚀 本地运行

### 环境要求

| 工具 | 版本 |
|:-----|:-----|
| JDK | 17+ |
| MySQL | 8.0 |
| Gradle | 9.5+ |

### 步骤

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE chitu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 修改数据库配置
# 编辑 src/main/resources/application.properties
# spring.datasource.username=你的用户名
# spring.datasource.password=你的密码

# 3. 启动
./gradlew bootRun

# 4. 验证
curl http://localhost:8080/api/test
# 返回: {"message":"Chitu backend is running!"}

# 5. Swagger 文档
# 浏览器打开 http://localhost:8080/swagger-ui.html
```

### 管理员账号

系统不支持直接注册管理员账号。需通过注册普通用户后，手动修改数据库：

```sql
UPDATE user SET role = 1 WHERE phone = '你的手机号';
```

---

## 📁 项目结构

```
src/main/kotlin/com/example/chitu/
├── ChituApplication.kt
│
├── config/
│   ├── CorsConfig.kt               # CORS 跨域配置
│   └── GlobalExceptionHandler.kt    # 全局异常处理
│
├── controller/                      # 10 个 Controller
│   ├── AuthController.kt            # 注册/登录/安全
│   ├── UserController.kt            # 个人信息
│   ├── UserSettingController.kt     # 用户设置
│   ├── UserPasswordController.kt    # 密码管理
│   ├── TripController.kt            # 行程同步/查询
│   ├── AdminUserController.kt       # 管理员用户管理
│   ├── AdminTripController.kt       # 管理员行程管理
│   ├── DriverStatisticsController.kt# 驾驶统计
│   ├── ReminderRecordController.kt  # 提醒记录
│   └── TestController.kt            # 健康检查
│
├── service/                         # 6 个 Service
│   ├── UserService.kt
│   ├── UserSettingService.kt
│   ├── TripService.kt
│   ├── AdminUserService.kt
│   ├── DriverStatisticsService.kt
│   └── ReminderRecordService.kt
│
├── mapper/                          # 6 个 Mapper (BaseMapper)
│
├── entity/                          # 6 个实体类
│   ├── User.kt
│   ├── UserProfile.kt
│   ├── UserSetting.kt
│   ├── TripLog.kt
│   ├── DriverStatistics.kt
│   └── ReminderRecord.kt
│
├── dto/                             # 12 个数据传输对象
│   ├── ApiResponse.kt               # 统一响应包装
│   ├── LoginRequest/Response.kt
│   ├── RegisterRequest.kt
│   ├── TripSyncRequest.kt
│   ├── TripVO.kt
│   ├── UpdateProfileRequest.kt
│   ├── UpdateSettingRequest.kt
│   ├── UserProfileResponse.kt
│   ├── UserSettingResponse.kt
│   ├── AdminUserListResponse.kt
│   └── AdminUserProfileResponse.kt
│
└── utils/
    └── JwtUtil.kt                   # JWT 工具类（HS256）
```

---

## ✨ 业务亮点

### 幂等同步

```kotlin
@Transactional
fun syncTrip(userId: Long, clientId: String, request: TripSyncRequest): Boolean {
    val existing = tripLogMapper.selectOne(
        QueryWrapper<TripLog>().eq("client_id", clientId)
    )
    if (existing != null) return true  // 已存在，直接返回成功
    // 插入新记录
}
```

UUID 作为幂等键，无需数据库唯一约束保障数据一致性。

### 行程逻辑删除

```kotlin
// 管理员删除 → 标记 deleted=1，数据保留
fun deleteTrip(tripId: Long): Boolean {
    return tripLogMapper.update(
        null, UpdateWrapper<TripLog>().eq("trip_id", tripId).set("deleted", 1)
    ) > 0
}
```

所有行程数据可追溯，不影响已有统计分析。

### 统一响应格式

```kotlin
data class ApiResponse<T>(val code: Int, val message: String, val data: T? = null) {
    companion object {
        fun <T> success(data: T? = null, message: String = "success") = ApiResponse(200, message, data)
        fun <T> error(code: Int = 400, message: String) = ApiResponse(code, message, null)
    }
}
```

所有接口返回统一 JSON 结构，Android 和 Admin 端通用解析。

---

## 🔗 相关仓库

| 仓库 | 说明 |
|:-----|:------|
| [chitu-android](https://github.com/ZonEn123/chitu-android) | Android 客户端 |
| [chitu-admin](https://github.com/ZonEn123/chitu-admin) | Vue3 管理后台 |

---

## 📸 API 文档截图

| Swagger 文档 | <!-- 待上传 --> |
|:-------------|:----------------|
<!-- Swagger 截图 -->

---

## 📄 License

MIT © 2026 Peng Zheng

---
