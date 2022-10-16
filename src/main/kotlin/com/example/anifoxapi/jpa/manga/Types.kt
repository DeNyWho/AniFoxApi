package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="types", schema = "manga")
data class Types(
    @Id
    val id: Long? = 0,

    val type: String = "",
    val year: String = "",
    val status: String = "",
    val limitation: String = ""
)