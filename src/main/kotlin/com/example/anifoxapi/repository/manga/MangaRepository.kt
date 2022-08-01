package com.example.anifoxapi.repository.manga

import com.example.anifoxapi.model.manga.Manga
import org.springframework.stereotype.Repository

@Repository
interface MangaRepository {

    fun search(query: String): List<Manga>

    fun popular(countPage: Int, status: Int?, countCard: Int?): List<Manga>

    fun newUpdate(countPage: Int, countCard: Int?): List<Manga>

    fun views(countPage: Int, countCard: Int?): List<Manga>

    fun details(url: String): Manga

    fun readMangaByLink(url: String): List<String>

}