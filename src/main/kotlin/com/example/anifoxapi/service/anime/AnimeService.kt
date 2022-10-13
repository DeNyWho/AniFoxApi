package com.example.anifoxapi.service.anime

import com.example.anifoxapi.repository.anime.AnimeRep
import com.example.anifoxapi.repository.anime.AnimeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AnimeService: AnimeRep {

    @Value("\${afa.app.anime_full}")
    lateinit var animeFull: String

    @Autowired
    lateinit var animeRepository: AnimeRepository

    override fun addDataToDB(){

    }

}