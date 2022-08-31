package com.example.anifoxapi.repository.user

import com.example.anifoxapi.jpa.user.RecoveryCode
import com.example.anifoxapi.jpa.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RecoveryCodeRepository : JpaRepository<RecoveryCode, Long> {
    fun findByUser(user: User): Optional<RecoveryCode>

    fun deleteByUser(user: User)
}