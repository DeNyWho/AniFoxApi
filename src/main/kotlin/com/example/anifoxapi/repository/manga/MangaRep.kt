package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.jpa.manga.Manga
import com.example.anifoxapi.model.manga.MangaLightPopularResponse
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.manga.TestMangaResponse
import org.hibernate.annotations.Parameter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface MangaRep {

    fun search(query: String): List<MangaLightResponse>

    fun readMangaByLink(url: String): List<String>

    fun test(): List<String>
    fun addPopularDataToDB(): Manga
    fun getMangaFromDB(id: Int): Manga
    fun getManga(countCard: Int, status: String?, page: Int, order: String?): List<MangaLightResponse>
    fun findByGenre(genre: String, countCard: Int, page: Int): List<TestMangaResponse>
}


@Repository
interface MangaRepository: PagingAndSortingRepository<Manga, Int> {

    @Query(value = "Select u From Manga u where :title member of u.genres.title")
    fun findByGenres(pageable: Pageable, @Param("title") genre: String): Page<Manga>

    override fun findById(id: Int): Optional<Manga>

    @Query(value = "SELECT u FROM Manga u where u.types.status = :status")
    fun findAllByPopularWithStatus(pageable: Pageable, @Param("status") status: String): Page<Manga>

    @Query(value = "SELECT u FROM Manga u order by u.views desc")
    fun findByReads(pageable: Pageable): Page<Manga>



    @Query(value = "SELECT u FROM Manga u where u.title LIKE %?1%")
    fun findByTitle(@Param("title") title: String): List<Manga>

}