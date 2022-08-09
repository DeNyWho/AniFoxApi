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
    @Column(columnDefinition = "TEXT")
    var description: String = "",
    @OneToOne(cascade = [CascadeType.ALL])
    var genres: Genres = Genres(),
    @OneToOne(cascade = [CascadeType.ALL])
    var types: Types = Types(),
    @OneToOne(cascade = [CascadeType.ALL])
    var info: Info = Info(),
    @OneToOne(cascade = [CascadeType.ALL])
    var chapters: Chapters = Chapters(),
    val chaptersCount: Int = 0,
    var rate: Double = 0.0,
    @Column(name = "count_rate")
    var countRate: Int = 0
)
