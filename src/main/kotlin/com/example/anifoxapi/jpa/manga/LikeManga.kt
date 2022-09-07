package com.example.anifoxapi.jpa.manga

import javax.persistence.*


@Entity
@Table(name="likeManga")
data class LikeManga(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = 0,
    @Column(columnDefinition = "TEXT")
    @ElementCollection
    val title: List<String> = ArrayList<String>(),
)