package com.example.anifoxapi.config.filter

import com.example.anifoxapi.config.jwt.JwtTokenUtils
import com.example.anifoxapi.model.user.dto.User
import com.example.anifoxapi.service.user.UserService
import com.example.anifoxapi.util.Constants.AUTHORIZED_HEADER
import com.example.anifoxapi.util.Constants.AUTHORIZED_TOKEN_ERROR_NOT_SET
import com.example.anifoxapi.util.Constants.UNAUTHORIZED_ERROR
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

//@Component
//class JwtRequestFilter: Filter {
//
//    @Autowired
//    private lateinit var jwtTokenUtils: JwtTokenUtils
//
//    @Autowired
//    private lateinit var userService: UserService
//
//    private val excludeUrls = ArrayList<String>()
//
//    private val excludeContainsUrls = ArrayList<String>()
//
//
//    init {
//        excludeUrls.add("/api2/user/login")
//        excludeUrls.add("/api2/user/register")
//    }
//
//
//    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
//        try {
//            val url = (request as HttpServletRequest).requestURI.lowercase(Locale.getDefault())
//
//            if(excludeUrls.stream().anyMatch { x -> url == x.lowercase(Locale.getDefault()) } ||
//                excludeContainsUrls.stream().anyMatch { x -> url.startsWith(x.lowercase(Locale.getDefault())) } ) {
//                chain!!.doFilter(request, response)
//                return
//            }
//
//            val requestTokenHeader = request.getHeader(AUTHORIZED_HEADER)
//
//            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer " ))
//                throw JwtException(AUTHORIZED_TOKEN_ERROR_NOT_SET)
//
//            val token = requestTokenHeader.substring(7)
//
//            val userName: String = jwtTokenUtils.getUsernameFromToken(token)
//            val userDto = userService.getUserByUsername(userName)!!
//            if (!jwtTokenUtils.validateToken(token, userDto))
//                throw JwtException("invalid token")
//            chain!!.doFilter(request, response)
//        } catch (ex: JwtException) {
//            (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED_ERROR)
//        } catch (ex: ExpiredJwtException) {
//            (response as HttpServletResponse).sendError(HttpServletResponse.SC_EXPECTATION_FAILED, ex.message)
//        } catch (ex: Exception) {
//            (response as HttpServletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.message)
//        }
//    }
//
//}