package com.example.anifoxapi.model.user.dto

import kotlinx.serialization.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="\"user\"")
data class User(
    @Id
    var id: Int = 0,
    var username: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var token: String = "",
)

fun User.convertToUser(): User {
    return User(id, username, password, firstName, lastName)
}
