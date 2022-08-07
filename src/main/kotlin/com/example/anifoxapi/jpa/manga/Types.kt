package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="types")
data class Types(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = 0,

    val type: String = "",
    val year: String = "",
    val status: String = "",
    val limitation: String = ""
)