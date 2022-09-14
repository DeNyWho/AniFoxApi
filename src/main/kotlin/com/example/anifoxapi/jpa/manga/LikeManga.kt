package com.example.anifoxapi.jpa.manga

import javax.persistence.*


@Entity
@Table(name="likeManga")
data class LikeManga(
    @Column(name = "manga_id")
    @Id
    val manga_id: Long? = 0,

    @Column(columnDefinition = "TEXT")
    @ElementCollection
    val title: List<String> = ArrayList<String>(),
)