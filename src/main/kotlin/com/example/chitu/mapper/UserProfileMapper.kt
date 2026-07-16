package com.example.chitu.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.example.chitu.entity.UserProfile
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserProfileMapper : BaseMapper<UserProfile>