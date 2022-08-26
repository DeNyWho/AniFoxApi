package com.example.anifoxapi.repository.user

import com.example.anifoxapi.jpa.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.*
import javax.transaction.Transactional

interface UserDetailsService : UserDetailsService {

    fun createVerificationTokenForUser(token: String, user: User)

    fun validateVerificationToken(token: String): String
}

interface UserRepository: JpaRepository<User, Long> {

//    @Query
//    fun FindingById()

    fun existsByUsername(@Param("username") username: String): Boolean

    fun findByUsername(@Param("username") username: String): Optional<User>


    @Query(value = "SELECT u FROM User u where u.token = :token")
    fun findByToken(@Param("token") token: String): Optional<User>

    fun findByEmail(@Param("email") email: String): Optional<User>

    @Transactional
    fun deleteByUsername(@Param("username") username: String)

}