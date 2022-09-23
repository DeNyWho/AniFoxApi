package com.example.anifoxapi.util

import com.example.anifoxapi.model.ScrapePageMain
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachAttribute
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div

//fun scrapePages(page: Int): ScrapePageMain {
//    val urls = mutableListOf<String>()
//    val images = mutableListOf<String>()
//    val titles = mutableListOf<String>()
//    skrape(HttpFetcher) {
//        request {
//            url = "https://mangahub.ru/explore/type-is-nor-comix/genres-is-nor-erotica/sort-is-date?page=$page"
//            timeout = 400_000
//        }
//        response {
//            //title
//            document.div {
//                withClass = "text-line-clamp.mt-2"
//                titles.addAll(findAll{ return@findAll eachText})
//            }
//            //urls
//            document.a {
//                withClass = "d-block.rounded.fast-view-layer"
//                urls.addAll(findAll{ return@findAll eachHref}.map { "https://mangahub.ru$it"} )
//            }
//            //image
//            document.div {
//                withClass = "comic-grid-image"
//                images.addAll(findAll { return@findAll eachAttribute("data-background-image") }.map { "https://mangahub.ru$it" })
//            }
//        }
//    }
//}