package com.example.anifoxapi.controller.auth

import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.jwt.JwtProvider
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
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginUser, response: HttpServletResponse): ResponseEntity<*> {

        val userCandidate: Optional<User> = userRepository.findByUsername(loginRequest.username!!)

        if (userCandidate.isPresent) {
            val user: User = userCandidate.get()

            if (!user.enabled) {
                return ResponseEntity(
                    "Account is not verified yet! Please, follow the link in the confirmation email.",
                    HttpStatus.UNAUTHORIZED
                )
            }

            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            )
            SecurityContextHolder.getContext().setAuthentication(authentication)
            val jwt: String = jwtProvider.generateJwtToken(user.username!!)

            val cookie = Cookie(authCookieName, jwt)
            cookie.maxAge = jwtProvider.jwtExpiration!!
            cookie.secure = isCookieSecure
            cookie.isHttpOnly = true
            cookie.path = "/"
            response.addCookie(cookie)

            val authorities: List<GrantedAuthority> =
                user.roles!!.stream().map { role -> SimpleGrantedAuthority(role.name) }
                    .collect(
                        Collectors.toList<GrantedAuthority>()
                    )
            return ResponseEntity.ok(SuccessfulSigninResponse(user.username, authorities))
        } else {
            return ResponseEntity(
                "User not found!",
                HttpStatus.BAD_REQUEST
            )
        }
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody newUser: NewUser): ResponseEntity<*> {

        val userCandidate: Optional<User> = userRepository.findByUsername(newUser.username!!)

        if (!userCandidate.isPresent) {
            if (usernameExists(newUser.username!!)) {
                return ResponseEntity(
                    "Username is already taken!",
                    HttpStatus.BAD_REQUEST
                )
            } else if (emailExists(newUser.email!!)) {
                return ResponseEntity(
                    "Email is already in use!",
                    HttpStatus.BAD_REQUEST
                )
            }

            try {

                val user = User(
                    0,
                    newUser.username!!,
                    newUser.firstName!!,
                    newUser.lastName!!,
                    newUser.email!!,
                    encoder.encode(newUser.password),
                    false
                )

            user.roles = listOf(roleRepository.findByName("ROLE_USER"))

            println("USER = $user")

            val registeredUser = userRepository.save(user)

                println("USER = $user")

                emailService.sendRegistrationConfirmationEmail(registeredUser)
            } catch (e: Exception) {
                return ResponseEntity(
                    "${e.message}",
                    HttpStatus.SERVICE_UNAVAILABLE
                )
            }

            return ResponseEntity(
                "Please, follow the link in the confirmation email to complete the registration.",
                HttpStatus.OK
            )
        } else {
            return ResponseEntity(
                "User already exists!",
                HttpStatus.BAD_REQUEST
            )
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
