package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="manga")
data class Manga(
    @Id
    val id: Int = 0,
    var title: String = "",
    var image: String = "",
    var url: String = "",
    var description: String = "",
    @OneToOne
    val genres: Genres = Genres(),
    val types: String = "",
    @OneToOne
    var info: Info = Info(),
    @OneToOne
    val chapters: Chapters = Chapters()
)
