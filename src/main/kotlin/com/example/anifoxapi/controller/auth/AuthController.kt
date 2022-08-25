package com.example.anifoxapi.controller.auth

import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.jpa.user.UserResponseDto
import com.example.anifoxapi.jwt.JwtProvider
import com.example.anifoxapi.model.responses.ServiceResponse
import com.example.anifoxapi.model.responses.SuccessfulSigninResponse
import com.example.anifoxapi.model.user.LoginUser
import com.example.anifoxapi.model.user.NewUser
import com.example.anifoxapi.repository.user.RoleRepository
import com.example.anifoxapi.repository.user.UserRepository
import com.example.anifoxapi.service.user.EmailService
import com.example.anifoxapi.service.user.UserService
import com.example.anifoxapi.service.user.UserService.Companion.TOKEN_EXPIRED
import com.example.anifoxapi.service.user.UserService.Companion.TOKEN_INVALID
import com.example.anifoxapi.service.user.UserService.Companion.TOKEN_VALID
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.io.UnsupportedEncodingException
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@Tag(name = "Authorization API", description = "All about user authorization")
@RequestMapping("/api2/auth/")
class AuthController {

    @Value("\${afa.app.authCookieName}")
    lateinit var authCookieName: String

    @Value("\${afa.app.isCookieSecure}")
    var isCookieSecure: Boolean = true

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtProvider: JwtProvider

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var emailService: EmailService

    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginUser, response: HttpServletResponse): ServiceResponse<UserResponseDto?> {

        val userCandidate: Optional<User> = userRepository.findByUsername(loginRequest.username!!)

        if (userCandidate.isPresent) {
            val user: User = userCandidate.get()

//            if (!user.enabled) {
//                return ResponseEntity(
//                    "Account is not verified yet! Please, follow the link in the confirmation email.",
//                    HttpStatus.UNAUTHORIZED
//                )
//            }

            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            )
            SecurityContextHolder.getContext().authentication = authentication
            val jwt: String = jwtProvider.generateJwtToken(user.username!!)

            val cookie = Cookie(authCookieName, jwt)
            cookie.maxAge = jwtProvider.jwtExpiration!!
            cookie.secure = isCookieSecure
            cookie.isHttpOnly = true
            cookie.path = "/"
            response.addCookie(cookie)

//            val authorities: List<GrantedAuthority> =
//                user.roles!!.stream().map { role -> SimpleGrantedAuthority(role.name) }
//                    .collect(
//                        Collectors.toList<GrantedAuthority>()
//                    )
            return ServiceResponse(
                data = listOf(UserResponseDto(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    password = user.password,
                    enabled = user.enabled,
                    token = user.token,
                    roles = user.roles,
                    created = user.created
                )),
                status = HttpStatus.OK,
                message = cookie.value
            )
        } else {
            return ServiceResponse(
                data = null,
                status = HttpStatus.BAD_REQUEST,
                message = "User not found!"
            )
        }
    }

    @PostMapping("/findUserByToken")
    fun findUserByToken(@RequestParam token: String): ServiceResponse<UserResponseDto> {
        val user = userRepository.findByToken(token).get()
        return ServiceResponse(data = listOf(UserResponseDto(
            id = user.id,
            username = user.username,
            email = user.email,
            password = user.password,
            enabled = user.enabled,
            token = user.token,
            roles = user.roles,
            created = user.created
        )
        ), status = HttpStatus.OK)
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody newUser: NewUser): ServiceResponse<UserResponseDto?> {

        val userCandidate: Optional<User> = userRepository.findByUsername(newUser.username!!)

        if (!userCandidate.isPresent) {
            if (usernameExists(newUser.username!!)) {
                return ServiceResponse(
                    data = null,
                    status = HttpStatus.BAD_REQUEST,
                    message = "Username is already taken!"
                )
            } else if (emailExists(newUser.email!!)) {
                return ServiceResponse(
                    data = null,
                    status = HttpStatus.BAD_REQUEST,
                    message = "Email is already in use!"
                )
            }
            val token = UUID.randomUUID().toString()
            var user: User = User()
            try {

                user = User(
                    id = 0,
                    username = newUser.username!!,
                    email = newUser.email!!,
                    password = encoder.encode(newUser.password),
                    enabled = false,
                    token = token,
                    created = LocalDateTime.now()
                )

            user.roles = listOf(roleRepository.findByName("ROLE_USER"))

            userRepository.save(user)

            } catch (e: Exception) {
                return ServiceResponse(
                    data = null,
                    status = HttpStatus.SERVICE_UNAVAILABLE,
                    message = "${e.message}"
                )
            }

            return ServiceResponse(
                data = listOf(UserResponseDto(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    password = user.password,
                    enabled = user.enabled,
                    token = user.token,
                    roles = user.roles,
                    created = user.created
                )),
                status = HttpStatus.OK,
                message = "Registration completed!"
            )
        } else {
            return ServiceResponse(
                data = null,
                status = HttpStatus.BAD_REQUEST,
                message = "User already exists!"
            )
        }
    }

    @PostMapping("/confirmEmail")
    fun confirmRegistration(@Valid @RequestParam token: String): String {
        val registeredUser = userRepository.findByToken(token).get()

        return try {
            emailService.sendRegistrationConfirmationEmail(registeredUser)
            "email confirmed"
        } catch (e: Exception){
            "Erorr: ${e.message}"
        }

    }

    @GetMapping("/registrationConfirm")
    @CrossOrigin(origins = ["*"])
    @Throws(UnsupportedEncodingException::class)
    fun confirmRegistration(request: HttpServletRequest, model: Model, @RequestParam("token") token: String): ResponseEntity<*> {

        when(userService.validateVerificationToken(token)) {
            TOKEN_VALID -> return ResponseEntity.ok("Registration confirmed")
            TOKEN_INVALID -> return ResponseEntity("Token is invalid!", HttpStatus.BAD_REQUEST)
            TOKEN_EXPIRED -> return ResponseEntity("Token is invalid!", HttpStatus.UNAUTHORIZED)
        }

        return ResponseEntity("Server error. Please, contact site owner", HttpStatus.SERVICE_UNAVAILABLE)
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<*> {
        val cookie = Cookie(authCookieName, null)
        cookie.maxAge = 0
        cookie.secure = isCookieSecure
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)

        return ResponseEntity.ok("Successfully logged")
    }

    private fun emailExists(email: String): Boolean {
        return userRepository.findByUsername(email).isPresent
    }

    private fun usernameExists(username: String): Boolean {
        return userRepository.findByUsername(username).isPresent
    }

}
