package com.example.anifoxapi.repository.user

import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.jpa.user.UserResponseDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.*
import javax.transaction.Transactional

interface UserDetailsService : UserDetailsService {

    fun createVerificationTokenForUser(token: String, user: User)

    fun validateVerificationToken(token: String): String
    fun changeUserPassword(email: String, password: String)
}

interface UserRepository: JpaRepository<User, Long> {


    fun findByUsername(@Param("username") username: String): Optional<User>


    @Query(value = "SELECT u FROM User u where u.token = :token")
    fun findByToken(@Param("token") token: String): Optional<User>

    @Query(value = "SELECT u FROM User u where u.email = :email")
    fun findByEmail(@Param("email") email: String): Optional<User>


}