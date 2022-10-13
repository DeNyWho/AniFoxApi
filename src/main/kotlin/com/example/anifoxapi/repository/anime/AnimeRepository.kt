package com.example.anifoxapi.repository.anime

import com.example.anifoxapi.jpa.anime.Anime
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AnimeRepository: PagingAndSortingRepository<Anime, Int> {


}