package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="info")
data class Info(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = 0,

    @ElementCollection
    val name: List<String> = ArrayList<String>(),

    @ElementCollection
    val value: List<String> = ArrayList<String>(),
)