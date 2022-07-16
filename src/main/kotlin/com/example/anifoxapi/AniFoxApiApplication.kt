package com.example.anifoxapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
class AniFoxApiApplication

fun main(args: Array<String>) {
    runApplication<AniFoxApiApplication>(*args)
}
