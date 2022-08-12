package com.example.anifoxapi.controller.user

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "User API", description = "All about user")
@RequestMapping("/api2/user/")
class UserController {

}