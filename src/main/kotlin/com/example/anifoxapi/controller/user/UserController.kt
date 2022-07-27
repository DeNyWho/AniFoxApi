package com.example.anifoxapi.controller.user

import com.example.anifoxapi.config.jwt.JwtTokenUtils
import com.example.anifoxapi.model.responses.ServiceResponse
import com.example.anifoxapi.model.user.dto.User
import com.example.anifoxapi.model.user.dto.convertToUser
import com.example.anifoxapi.service.user.UserService
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import com.example.anifoxapi.util.UserUtil.Companion.getCurrentUsername
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api2/user")
class UserController {

    @Autowired
    lateinit var service: UserService

    @Autowired
    lateinit var jwtUtil: JwtTokenUtils

    @GetMapping()
    @Operation(summary = "Get user by id")
    fun getUserById(request: HttpServletRequest): ServiceResponse<User> {
        return try {
            val currentUser = getCurrentUsername(request, jwtUtil)
            val data = service.getUserByUsername(currentUser) ?: throw Exception("data not found")
            ServiceResponse(data = listOf(data), status = HttpStatus.OK)
        } catch (e: NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authorization")
    fun login(@RequestBody user: User): ServiceResponse<User> {
        return try {
            val data =
                service.getByUsernameAndPass(user.username, user.password) ?: throw Exception("data not found")
            data.token = jwtUtil.generateToken(data)!!
            ServiceResponse(listOf(data), HttpStatus.OK)
        } catch (e: NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Sign up")
    fun addUser(@RequestBody user: User): ServiceResponse<User> {
        return try {
            val data = service.insert(user.convertToUser())
            ServiceResponse(listOf(data), HttpStatus.OK)
        } catch (e: NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }
}