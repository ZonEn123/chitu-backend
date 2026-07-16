package com.example.chitu.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.example.chitu.entity.User
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper : BaseMapper<User>