package com.example.anifoxapi.config

//import com.example.anifoxapi.config.filter.JwtRequestFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean

@Configurable
class JwtFilterConfig {

//    @Autowired
//    private lateinit var jwtRequestFilter: JwtRequestFilter

//    @Bean
//    fun jwtFilterRegister(): FilterRegistrationBean<*> {
//        val filterRegistrationBean = FilterRegistrationBean<JwtRequestFilter>()
//        filterRegistrationBean.filter = jwtRequestFilter
//        filterRegistrationBean.addUrlPatterns("/api2/*")
//        filterRegistrationBean.setName("jwtFilter")
//        filterRegistrationBean.order = 1
//        return filterRegistrationBean
//    }
} 