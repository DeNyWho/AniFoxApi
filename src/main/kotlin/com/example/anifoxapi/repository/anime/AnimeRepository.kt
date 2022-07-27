package com.example.anifoxapi.repository.anime

import com.example.anifoxapi.model.user.dto.User
import org.springframework.stereotype.Repository

@Repository
interface AnimeRepository {

    fun findFirstByUsernameAndPassword(username: String, password: String): User?

    fun findUserByUsername(username: String): User?
}