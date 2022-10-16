package com.example.anifoxapi.service.anime

import com.example.anifoxapi.repository.anime.AnimeRep
import com.example.anifoxapi.repository.anime.AnimeRepository
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachAttribute
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AnimeService: AnimeRep {

    @Value("\${afa.app.anime_full}")
    lateinit var animeFull: String

    @Autowired
    lateinit var animeRepository: AnimeRepository

    private var pagesBoolean = false

    override fun addDataToDB(){
        val urls = mutableListOf<String>()
        var allGenres = mutableListOf<String>()
        val pageSize = 108

        for ( i in 1 until 20) {
            pagesBoolean = false
            while (!pagesBoolean) {
                try {
                    skrape(HttpFetcher) {
                        request {
                            url =
                                "$animeFull$i"
                            timeout = 400_000
                        }
                        response {
                            document.div {
                                withClass = "h5.font-weight-normal.mb-1"
                                this.a {
                                    urls.addAll(findAll { return@findAll eachHref })
                                }
                            }

                            if(i == 1){
                                document.span {
                                    withClass = "custom-control.custom-checkbox.cursor-pointer.d-block.mr-0.fpr-negate"
                                    allGenres.addAll(findAll { return@findAll eachText })
                                }
                            }
                        }
                    }
                    pagesBoolean = true
                } catch (e: Exception) {
                    pagesBoolean = false
                }
            }
        }

        urls.forEach {
            pagesBoolean = false
            var title = ""
            var description = ""
            var image = ""
            val footage = mutableListOf<String>()
            val trailer = mutableListOf<String>()
            val trailerBackground = mutableListOf<String>()
            val genres = mutableListOf<String>()

            println(it)

            while(!pagesBoolean) {
                try {
                    skrape(HttpFetcher) {
                        request {
                            url = it
                            timeout = 400_000
                        }
                        response {
                            document.div {
                                withClass = "anime-title"
                                title = findFirst { return@findFirst text }
                            }

                            document.div {
                                withClass = "anime-poster.position-relative.cursor-pointer"
                                this.img {
                                    image = findFirst { return@findFirst attribute("src") }
                                }
                            }

                            document.div {
                                withClass = "description.pb-3"
                                description = findFirst { return@findFirst text }
                            }

                            document.div {
                                try {
                                    withClass = "d-flex.screenshots-block"
                                    this.a {
                                        footage.addAll(findAll { return@findAll eachHref })
                                    }
                                } catch (e: Exception){
                                    footage.add("Empty")
                                }
                            }

                            document.div {
                                try {
                                    withClass = "video-block"
                                    this.a {
                                        trailer.addAll(findAll { return@findAll eachHref })
                                        trailerBackground.addAll(findAll { return@findAll eachAttribute("data-original") })
                                    }
                                } catch (e: Exception){
                                    trailer.add("Empty")
                                    trailerBackground.add("Empty")
                                }
                            }

                            document.dd {
                                withClass = "col-6.col-sm-8.mb-1.overflow-h"
                                this.a {
                                    genres.addAll(findAll { return@findAll eachText })
                                    genres.removeAll {item ->
                                        item !in allGenres
                                    }

                                    if(genres.size == 0){
                                        genres.add("Empty")
                                    }

                                }
                            }

                            document.dd {
                                withClass = "col-6.col-sm-8.mb-1"
                                this.span {
                                    println("йнкбн яепхи = ${findAll { return@findAll eachText }}")
                                }

                            }


                        }
                    }
                    pagesBoolean = true
                } catch (e: Exception){
                    println(e.message)
                    pagesBoolean = false
                }
            }
        }

    }

}