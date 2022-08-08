package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.jpa.manga.Manga
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MangaRep {

    fun search(query: String): List<MangaLightResponse>

    fun readMangaByLink(url: String): List<String>

    fun manga(countPage: Int, status: Int?, countCard: Int?, sort: String?): List<MangaLightResponse>
    fun test(): List<String>
    fun addPopularDataToDB(): Manga
    fun getMangaFromDB(id: Int): Manga
}

@Repository
interface MangaRepository: JpaRepository<Manga, Long> {

    fun findById(id: Int): Optional<Manga>


}