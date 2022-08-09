package com.example.anifoxapi.model.manga

data class MangaLightResponse(
    var id: Int = 0,
    var title: String = "",
    var image: String = "",
    val url: String
)