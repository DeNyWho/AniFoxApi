package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="chapters", schema = "manga")
data class Chapters (
    @Id
    val id: Long? = 0,

    @ElementCollection
    @JoinTable(schema = "manga", name = "chapter_title")
    val title: List<String> = ArrayList<String>(),

    @ElementCollection
    @JoinTable(schema = "manga", name = "chapter_url")
    val url: List<String> = ArrayList<String>(),

    @ElementCollection
    @JoinTable(schema = "manga", name = "chapter_date")
    val date: List<String> = ArrayList<String>(),
)