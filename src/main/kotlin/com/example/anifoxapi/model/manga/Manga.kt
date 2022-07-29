package com.example.anifoxapi.model.manga

data class Manga(
    var title: String = "",
    var image: String = "",
    var url: String = "",
    val description: String = "",
    val tags: String = "",
    val list: MangaTags = MangaTags(),
    val chapters: MangaChapters = MangaChapters(listOf(""),listOf(""))
)
