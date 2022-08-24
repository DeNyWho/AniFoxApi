package com.example.anifoxapi.service.user

import com.example.anifoxapi.jpa.manga.Manga
import com.example.anifoxapi.jpa.manga.MangaResponseDto
import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.model.PageableData
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.responses.PageableResponse
import com.example.anifoxapi.model.user.FavouriteDto
import com.example.anifoxapi.repository.manga.MangaRepository
import com.example.anifoxapi.repository.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class FavouriteService(
    private val userRepository: UserRepository,
    private val mangaRepository: MangaRepository,
) {
    fun toDto(manga: Manga): MangaResponseDto {
        return MangaResponseDto(
            id = manga.id,
            title = manga.title,
            image = manga.image,
            url = manga.url,
            description = manga.description,
            genres = manga.genres,
            types = manga.types,
            info = manga.info,
            chapters = manga.chapters,
            chaptersCount = manga.chaptersCount,
            views = manga.views,
            rate = manga.rate,
            countRate = manga.countRate
        )
    }

    fun findByUsername(username: String): User {
        return userRepository.findByToken(username)
            .orElseThrow { UsernameNotFoundException("No user found by username: $username") }
    }
    fun getUserMangaByUserId(
        id: Long, page: Int, size: Int
    ): PageableResponse<MangaLightResponse> {
        val user = userRepository.findById(id).orElseThrow { UsernameNotFoundException("No user found by this id: $id") }

        val result = mangaRepository.findMangasByUsers(user, PageRequest.of(page - 1, size))

        val temp = mutableListOf<MangaLightResponse>()
        val list = result.content
        println("LIST = $list")
        for(i in 0 until list.size) {
            temp.add(
                MangaLightResponse(
                    id = list[i].id,
                    title = list[i].title,
                    image = list[i].image,
                    url = list[i].url,
                    description = list[i].description,
                    rate = list[i].rate,
                    countRate = list[i].countRate
                )
            )
        }
        println("HUCH = $temp")
        return PageableResponse(
            temp.toList(),
            PageableData(
                result.totalElements,
                page.toLong(),
                size.toLong(),
                result.size.toLong()
            )
        )
    }

    fun addFavourite(dto: FavouriteDto, status: String) {
        val manga = mangaRepository.findById(dto.mangaId).get()
        val user = userRepository.getById(dto.userId)
            .addToFavourite(manga)
//        when(status){
//            "holdOn" -> userRepository.getById(dto.userId).addToOnHold(manga)
//            "watching" -> userRepository.getById(dto.userId).addToWatching(manga)
//            "completed" -> userRepository.getById(dto.userId).addToCompleted(manga)
//        }
        userRepository.save(user)
    }

    fun removeFavourite(dto: FavouriteDto) {
        val manga = mangaRepository.findById(dto.mangaId).get()
        val user = userRepository.getById(dto.userId)
            .removeFromFavourite(manga)
        userRepository.save(user)
    }
}