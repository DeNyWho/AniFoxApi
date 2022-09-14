package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="linked")
data class Linked (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = 0,

    @ElementCollection
    val title: List<String> = ArrayList<String>(),
)