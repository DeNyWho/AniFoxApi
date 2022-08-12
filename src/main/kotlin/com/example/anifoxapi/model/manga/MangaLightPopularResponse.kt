package com.example.anifoxapi.model.manga

data class MangaLightPopularResponse(
    var id: Int = 0,
    var title: String = "",
    var image: String = "",
    val url: String,
    var rate: Double,
    var countRate: Int
)