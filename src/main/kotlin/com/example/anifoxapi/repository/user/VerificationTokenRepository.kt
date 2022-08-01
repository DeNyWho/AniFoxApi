package com.example.anifoxapi.repository.user

import com.example.anifoxapi.jpa.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface VerificationTokenRepository : JpaRepository<VerificationToken, Long> {
    fun findByToken(token: String): Optional<VerificationToken>
}