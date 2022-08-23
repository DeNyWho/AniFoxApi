package com.example.anifoxapi.model.user

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class NewUser : Serializable {

    @JsonProperty("username")
    var username: String? = null

    @JsonProperty("email")
    var email: String? = null

    @JsonProperty("password")
    var password: String? = null

    constructor() {}

    constructor(username: String, email: String, password: String ) {
        this.username = username
        this.email = email
        this.password = password
    }

    companion object {

        private const val serialVersionUID = -1764970284520387975L
    }
}