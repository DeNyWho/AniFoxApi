package com.example.anifoxapi.model.anime

data class Anime(
    var title: String = "",
    var image: String = "",
    var url: String = "",
)

fun Anime.convertToAnime(): Anime {
    return Anime(title, url, image)
}
