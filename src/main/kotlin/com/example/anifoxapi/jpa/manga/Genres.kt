package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="genres")
data class Genres (
    @Id
    val id: Long? = 0,

    @Column(columnDefinition = "TEXT")
    @ElementCollection
    val title: List<String> = ArrayList<String>()
)