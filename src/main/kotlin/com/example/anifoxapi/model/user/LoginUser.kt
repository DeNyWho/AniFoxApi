package com.example.anifoxapi.model.user

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class LoginUser : Serializable {

    @JsonProperty("email")
    var email: String? = null

    @JsonProperty("password")
    var password: String? = null

    constructor() {}

    constructor(email: String, password: String) {
        this.email = email
        this.password = password
    }

    companion object {

        private const val serialVersionUID = -1764970284520387975L
    }
}