package com.example.anifoxapi.service.user

import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.jpa.user.VerificationToken
import com.example.anifoxapi.repository.user.UserDetailsService
import com.example.anifoxapi.repository.user.UserRepository
import com.example.anifoxapi.repository.user.VerificationTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class UserService: UserDetailsService {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var tokenRepository: VerificationTokenRepository


    override fun changeUserPassword (email: String, password: String) {
        val user = userRepository.findByEmail(email).get()
        user.password = password

        userRepository.save(user)

    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username).get()

        val authorities: List<GrantedAuthority> = user.roles!!.stream().map { role -> SimpleGrantedAuthority(role.name) }
            .collect(
            Collectors.toList<GrantedAuthority>())

        return org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }

    override fun createVerificationTokenForUser(token: String, user: User) {
        tokenRepository.save(VerificationToken(token, user))
    }


    override fun validateVerificationToken(token: String): String {
        val verificationToken: Optional<VerificationToken> = tokenRepository.findByToken(token)

        if (verificationToken.isPresent) {
            val user: User? = verificationToken.get().user
            val cal: Calendar = Calendar.getInstance()
            if ((verificationToken.get().expiryDate!!.time - cal.time.time) <= 0) {
                tokenRepository.delete(verificationToken.get())
                return TOKEN_EXPIRED
            }

            user!!.enabled = true
            tokenRepository.delete(verificationToken.get())
            userRepository.save(user)
            return TOKEN_VALID
        } else {
            return TOKEN_INVALID
        }

    }

    companion object {
        const val TOKEN_VALID: String = "valid"
        const val TOKEN_INVALID: String = "invalid"
        const val TOKEN_EXPIRED: String = "expired"
    }
}