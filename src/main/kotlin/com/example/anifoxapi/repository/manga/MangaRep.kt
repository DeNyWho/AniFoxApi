package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.jpa.manga.*
import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.model.manga.MangaLightResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface MangaRep {

    fun search(query: String): List<MangaLightResponse?>

    fun readMangaByLink(url: String): List<String>

    fun addDataToDB(): Manga
    fun getMangaFromDB(id: Int): MangaResponseDto
    fun getManga(countCard: Int, status: String?, page: Int, order: String?, genre: String?): List<MangaLightResponse>

    fun similarManga(id: Int): List<MangaLightResponse>
    fun linkedManga(id: Int): List<MangaLightResponse>
    fun genres(): List<String>
}

@Repository
interface GenreRepository: PagingAndSortingRepository<Genres, Int> {

    @Query(value = "SELECT distinct g FROM Genres g")
    fun wtf(): List<Genres>
}

@Repository
interface MangaRepository: PagingAndSortingRepository<Manga, Int> {

    fun findMangasByUsers(user: User, pageable: Pageable): Page<MangaResponseDto>

    fun findMangasByCompletedMangaUsers(user: User, pageable: Pageable): Page<MangaResponseDto>

    fun findMangasByWatchMangaUsers (user: User, pageable: Pageable): Page<MangaResponseDto>

    fun findMangasByOnHoldMangaUsers (user: User, pageable: Pageable): Page<MangaResponseDto>

    @Query(value = "Select u From Manga u where :title member of u.genres.title")
    fun findByGenres(pageable: Pageable, @Param("title") genre: String): Page<Manga>

//    @Query("select g.genres.title from Manga g")
//    fun findUniqueGenres(): List<String>

    @Query(value = "Select u From Manga u where u.types.status = :status")
    fun findByStatus(pageable: Pageable, @Param("status") status: String): Page<Manga>

    @Query(value = "Select u From Manga u where u.types.status = :status and :title member of u.genres.title")
    fun findByStatusAndGenre(pageable: Pageable, @Param("status") status: String, @Param("title") genre: String): Page<Manga>

    @Query(value = "Select u From LikeManga u where u.manga_id = :mangaID")
    fun findByLikeManga(@Param("mangaID") mangaID: Long): LikeManga

    @Query(value = "Select u From Linked u where u.id = :mangaID")
    fun findByLinkedManga(@Param("mangaID") mangaID: Long): Linked

    override fun findById(id: Int): Optional<Manga>

    @Query(nativeQuery = true, value = "SELECT * FROM Manga order by random()")
    fun findByRandom(pageable: Pageable): Page<Manga>

    @Query("select u from Manga u where upper(u.title) like concat('%', upper(?1), '%')")
    fun findByTitleSearch(@Param("title") title: String): List<Manga>

    @Query(value = "SELECT u FROM Manga u where u.title = :title")
    fun findByTitle(@Param("title") title: String): List<Manga>

}



