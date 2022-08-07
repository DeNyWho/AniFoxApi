package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.jpa.manga.Manga
import com.example.anifoxapi.jpa.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*
import javax.transaction.Transactional

@Repository
interface MangaRep {

    fun search(query: String): List<MangaLightResponse>

//    fun newUpdate(countPage: Int, countCard: Int?): List<MangaLightResponse>
//
//    fun views(countPage: Int, countCard: Int?): List<MangaLightResponse>

//    fun details(url: String): Manga

    fun readMangaByLink(url: String): List<String>

    fun manga(countPage: Int, status: Int?, countCard: Int?, sort: String?): List<MangaLightResponse>
    fun test(): List<String>
//    fun addPopularDataToDB(): Boolean
    fun addPopularDataToDB(): Manga
}

@Repository
interface MangaRepository: JpaRepository<Manga, Long> {

    fun findById(id: Int): Optional<Manga>


}