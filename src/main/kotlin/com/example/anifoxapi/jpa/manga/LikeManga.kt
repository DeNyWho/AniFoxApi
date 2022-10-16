package com.example.anifoxapi.jpa.manga

import javax.persistence.*


@Entity
@Table(name="likeManga", schema = "manga")
data class LikeManga(
    @Column(name = "manga_id")
    @Id
    val manga_id: Long? = 0,

    @Column(columnDefinition = "TEXT")
    @ElementCollection
    @JoinTable(schema = "manga", name = "like_title")
    val title: List<String> = ArrayList<String>(),
)