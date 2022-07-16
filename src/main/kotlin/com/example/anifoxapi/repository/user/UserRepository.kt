package com.example.anifoxapi.repository.user

import com.example.anifoxapi.model.user.dto.User
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : PagingAndSortingRepository<User, Int> {

    fun findFirstByUsernameAndPassword(username: String, password: String): User?

    fun findUserByUsername(username: String): User?

}