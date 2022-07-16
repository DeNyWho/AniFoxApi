package com.example.anifoxapi.util

import com.example.anifoxapi.config.jwt.JwtTokenUtils
import javax.servlet.http.HttpServletRequest
import java.util.*

class UserUtil {
    companion object {
        fun getCurrentUsername(request: HttpServletRequest, jwtUtil: JwtTokenUtils): String {
            val header = request.getHeader("Authorization")
            if (header == null || !header.lowercase(Locale.getDefault()).startsWith("bearer"))
                throw Exception("please set bearer token")
            val token = header.substring(7)
            return jwtUtil.getUsernameFromToken(token)
        }
    }
}