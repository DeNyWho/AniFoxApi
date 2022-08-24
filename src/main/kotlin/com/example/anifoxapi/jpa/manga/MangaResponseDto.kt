package com.example.anifoxapi.jpa.manga

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.OneToOne

@JsonIgnoreProperties(ignoreUnknown = true)
class MangaResponseDto(
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
    var views: Int = 0,
    var rate: Double = 0.0,
    @Column(name = "count_rate")
    var countRate: Int = 0,
)

