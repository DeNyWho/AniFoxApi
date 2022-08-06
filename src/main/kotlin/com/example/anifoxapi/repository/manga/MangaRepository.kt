package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.model.manga.Manga
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.manga.NewManga
import org.springframework.stereotype.Repository

@Repository
interface MangaRepository {

    fun search(query: String): List<MangaLightResponse>

//    fun newUpdate(countPage: Int, countCard: Int?): List<MangaLightResponse>
//
//    fun views(countPage: Int, countCard: Int?): List<MangaLightResponse>

    fun details(url: String): Manga

    fun readMangaByLink(url: String): List<String>

    fun manga(countPage: Int, status: Int?, countCard: Int?, sort: String?): List<MangaLightResponse>
    fun test(): List<String>
//    fun addPopularDataToDB(): Boolean
    fun addPopularDataToDB(): List<NewManga>
}