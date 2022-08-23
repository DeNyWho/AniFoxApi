package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="chapters")
data class Chapters (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = 0,

    @ElementCollection
    val title: List<String> = ArrayList<String>(),

    @ElementCollection
    val url: List<String> = ArrayList<String>(),

    @ElementCollection
    val date: List<String> = ArrayList<String>(),
)