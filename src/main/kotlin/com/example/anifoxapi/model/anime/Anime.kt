package com.example.anifoxapi.model.anime

import com.example.anifoxapi.model.manga.MangaChapters
import com.example.anifoxapi.model.manga.MangaTags

data class Anime(
    var title: String = "",
    var image: String = "",
    var url: String = "",
    val description: String = "",
    val tags: String = "",
    val list: MangaTags = MangaTags(),
    val chapters: MangaChapters = MangaChapters(listOf(""),listOf(""))
)

fun Anime.convertToAnime(): Anime {
    return Anime(title, url, image, list = list)
}
