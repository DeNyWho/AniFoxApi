package com.example.anifoxapi.model

data class ScrapePageMain(
    val urls: List<String>,
    var images: List<String>,
    var titles: List<String>
)