package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.jpa.manga.LikeManga
import com.example.anifoxapi.jpa.manga.Linked
import com.example.anifoxapi.jpa.manga.Manga
import com.example.anifoxapi.jpa.manga.MangaResponseDto
import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.model.manga.MangaLightResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface MangaRep {

    fun search(query: String): List<MangaLightResponse>

    fun readMangaByLink(url: String): List<String>

    fun addDataToDB(): Manga
    fun getMangaFromDB(id: Int): MangaResponseDto
    fun getManga(countCard: Int, status: String?, page: Int, order: String?, genre: String?): List<MangaLightResponse>

    fun similarManga(id: Int): List<MangaLightResponse>
    fun linkedManga(id: Int): List<MangaLightResponse>
}


@Repository
interface MangaRepository: PagingAndSortingRepository<Manga, Int> {

    fun findMangasByUsers(user: User, pageable: Pageable): Page<MangaResponseDto>

    fun findMangasByCompletedMangaUsers(user: User, pageable: Pageable): Page<MangaResponseDto>

    fun findMangasByWatchMangaUsers (user: User, pageable: Pageable): Page<MangaResponseDto>

    fun findMangasByOnHoldMangaUsers (user: User, pageable: Pageable): Page<MangaResponseDto>

    @Query(value = "Select u From Manga u where :title member of u.genres.title")
    fun findByGenres(pageable: Pageable, @Param("title") genre: String): Page<Manga>

    @Query(value = "Select u From Manga u where u.types.status = :status")
    fun findByStatus(pageable: Pageable, @Param("status") status: String): Page<Manga>

    @Query(value = "Select u From Manga u where u.types.status = :status and :title member of u.genres.title")
    fun findByStatusAndGenre(pageable: Pageable, @Param("status") status: String, @Param("title") genre: String): Page<Manga>

    @Query(value = "Select u From LikeManga u where u.manga_id = :mangaID")
    fun findByLikeManga(@Param("mangaID") mangaID: Long): LikeManga

    @Query(value = "Select u From Linked u where u.id = :mangaID")
    fun findByLinkedManga(@Param("mangaID") mangaID: Long): Linked

    @Query(value = "SELECT u FROM Manga u WHERE u.genres.id = :id")
    fun findByGenreID(@Param("id") id: String ): Manga

    override fun findById(id: Int): Optional<Manga>

    @Query(nativeQuery = true, value = "SELECT * FROM Manga order by random()")
    fun findByRandom(pageable: Pageable): Page<Manga>

    @Query(value = "SELECT u FROM Manga u where u.title LIKE %?1%")
    fun findByTitleSearch(@Param("title") title: String): List<Manga>

    @Query(value = "SELECT u FROM Manga u where u.title = :title")
    fun findByTitle(@Param("title") title: String): Manga

}

