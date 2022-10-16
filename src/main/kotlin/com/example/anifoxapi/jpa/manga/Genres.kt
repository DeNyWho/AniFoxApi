package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="genres", schema = "manga")
data class Genres (
    @Id
    val id: Long? = 0,

    @Column(columnDefinition = "TEXT")
    @ElementCollection
    @JoinTable(schema = "manga", name = "genre_title")
    val title: List<String> = ArrayList<String>()
)