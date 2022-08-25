package com.example.anifoxapi.service.user

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

    fun getUserMangaByUserId(
        id: Long, page: Int, size: Int, status: String?
    ): PageableResponse<MangaLightResponse> {
        val user = userRepository.findById(id).orElseThrow { UsernameNotFoundException("No user found by this id: $id") }
        val result = when(status){
            "completed" -> mangaRepository.findMangasByCompletedMangaUsers(user, PageRequest.of(page - 1, size))
            "watching" -> mangaRepository.findMangasByWatchMangaUsers(user, PageRequest.of(page - 1, size))
            "onHold" -> mangaRepository.findMangasByOnHoldMangaUsers(user, PageRequest.of(page - 1, size))
            else -> { mangaRepository.findMangasByUsers(user, PageRequest.of(page - 1, size)) }
        }

        val temp = mutableListOf<MangaLightResponse>()
        val list = result.content
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
        when(status){
            "holdOn" -> userRepository.getById(dto.userId).addToOnHold(manga)
            "watching" -> userRepository.getById(dto.userId).addToWatching(manga)
            "completed" -> userRepository.getById(dto.userId).addToCompleted(manga)
        }
        userRepository.save(user)
    }

    fun removeFavourite(dto: FavouriteDto) {
        val manga = mangaRepository.findById(dto.mangaId).get()
        val user = userRepository.getById(dto.userId)
            .removeFromFavourite(manga)
        userRepository.save(user)
    }
}