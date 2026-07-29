package com.example.chitu.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class CorsConfig {


    @Bean
    fun corsConfigurer(): WebMvcConfigurer {

        return object : WebMvcConfigurer {


            override fun addCorsMappings(registry: CorsRegistry) {


                registry.addMapping("/**")

                    // 允许Vue开发服务器
                    .allowedOrigins(
                        "http://localhost:5173"
                    )

                    // 允许请求方法
                    .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                    )

                    // 允许请求头
                    .allowedHeaders("*")


                    // 允许携带token
                    .allowCredentials(true)

            }

        }

    }

}