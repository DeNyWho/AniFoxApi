package com.example.anifoxapi.repository.anime

import org.springframework.stereotype.Repository

@Repository
interface AnimeRep {

    fun addDataToDB()
}