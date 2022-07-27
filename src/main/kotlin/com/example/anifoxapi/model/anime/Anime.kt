package com.example.anifoxapi.model.anime

data class Anime(
    var title: String = "",
    var page: String? = null,
    var image: String = "",
)

fun Anime.convertToAnime(): Anime {
    return Anime(title, page, image)
}
