package com.example.anifoxapi.service.user

import com.example.anifoxapi.model.user.dto.User
import com.example.anifoxapi.repository.user.UserRepository
import com.example.anifoxapi.util.SecurityUtil.Companion.encryptSHA256
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var repository: UserRepository


    fun getById(id: Int): User? {
        val data = repository.findById(id)
        return data.get()
    }

    fun getUserByUsername(username: String): User? {
        if (username.isEmpty())
            throw Exception("username is empty")
        return repository.findUserByUsername(username)
    }

    fun insert(data: User): User {
        if (data.id <= 0)
            throw Exception("invalid user id")
        if (data.username.isEmpty())
            throw Exception("username is empty")
        if (data.password.isEmpty())
            throw Exception("password is empty")
        if (data.firstName.isEmpty())
            throw Exception("firstName is empty")
        if (data.lastName.isEmpty())
            throw Exception("lastName is empty")
        val duplicate = getUserByUsername(data.username)
        if (duplicate != null)
            throw Exception("this username is already exists")
        val hashPass = encryptSHA256(data.password)
        data.password = hashPass
        val savedData = repository.save(data)
        savedData.password = ""
        return savedData
    }

    fun getByUsernameAndPass(userName: String, password: String): User? {
        if (userName.isEmpty() && password.isEmpty())
            throw Exception("username and password are empty")
        if (userName.isEmpty())
            throw Exception("username is empty")
        if (password.isEmpty())
            throw Exception("password is empty")
        val hashPass = encryptSHA256(password)
        return repository.findFirstByUsernameAndPassword(userName, hashPass)
    }

    fun getTotalCount(): Long {
        return repository.count()
    }

    fun changePassword(data: User, repeatPass: String, oldPass: String, currentUser: String): User {
        val user = repository.findUserByUsername(currentUser)
        if (user == null || data.id != user.id)
            throw Exception("you don't have permission")
        if (data.username.isEmpty())
            throw Exception("username is empty")
        if (oldPass.isEmpty())
            throw Exception("old pass is empty")
        if (data.password.isEmpty() || repeatPass.isEmpty())
            throw Exception("enter password and repeat")
        if (data.password != repeatPass)
            throw Exception("password and repeat are not match ")
        if (encryptSHA256(oldPass) != user.password)
            throw Exception("current pass is false")
        val hashPass = encryptSHA256(data.password)
        user.password = hashPass
        val savedData = repository.save(user)
        savedData.password = ""
        return savedData
    }

}