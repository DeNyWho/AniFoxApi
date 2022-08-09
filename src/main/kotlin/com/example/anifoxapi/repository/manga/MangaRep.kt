package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.jpa.manga.Manga
import com.example.anifoxapi.model.manga.MangaLightResponse
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

    fun search(query: String): List<Manga>

    fun readMangaByLink(url: String): List<String>

    fun test(): List<String>
    fun addPopularDataToDB(): Manga
    fun getMangaFromDB(id: Int): Manga
    fun getPopularManga(countCard: Int, status: String?, page: Int): List<Manga>
}


@Repository
interface MangaRepository: PagingAndSortingRepository<Manga, Int> {

    override fun findById(id: Int): Optional<Manga>

    @Query(value = "SELECT u FROM Manga u where u.types.status = :status")
    fun findAllByPopularWithStatus(pageable: Pageable, @Param("status") status: String): Page<Manga>

    @Query(value = "SELECT u FROM Manga u where u.title LIKE %?1%")
    fun findByTitle(@Param("title") title: String): List<Manga>

}