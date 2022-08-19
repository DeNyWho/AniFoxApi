package com.example.anifoxapi.service.manga

import com.example.anifoxapi.jpa.manga.*
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.manga.TestMangaResponse
import com.example.anifoxapi.repository.manga.MangaRep
import com.example.anifoxapi.repository.manga.MangaRepository
import com.example.anifoxapi.util.OS
import com.example.anifoxapi.util.getOS
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachAttribute
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.a
import it.skrape.selects.html5.div
import it.skrape.selects.html5.li
import it.skrape.selects.html5.span
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class MangaService: MangaRep {

    @Autowired
    lateinit var mangaRepository: MangaRepository


    override fun search(query: String): List<MangaLightResponse> {

        val light = mutableListOf<MangaLightResponse>()
        val manga = mangaRepository.findByTitle(query)

        manga.forEach {
            light.add(
                MangaLightResponse(
                    id = it.id,
                    title = it.title,
                    image = it.image,
                    url = it.url,
                    rate = it.rate,
                    countRate = it.countRate,
                    description = it.description
                )
            )
        }

        return light.toList()
    }

    override fun addPopularDataToDB(): Manga {
        var maxId = 0
        var pageSize = 0
        val urls = mutableListOf<String>()
        val images = mutableListOf<String>()
        val titles = mutableListOf<String>()
        var list = Manga()

        skrape(HttpFetcher) {
            request {
                url = "https://mangahub.ru/explore/type-is-nor-comix/genres-is-nor-erotica/sort-is-date?page=1"
            }
            response {
                document.span {
                    withClass = "badge.bg-primary.ms-1"
                    maxId = findAll { return@findAll eachText }[0].toInt()
                }
                document.li {
                    withClass = "page-item"
                    val pages = findAll { return@findAll eachText }
                    pageSize = pages[pages.lastIndex-1].toInt()
                }
            }
        }

        for ( i in 1 until 7) {
            println(i)
            skrape(HttpFetcher) {
                request {
                    url = "https://mangahub.ru/explore/type-is-nor-comix/genres-is-nor-erotica/sort-is-date?page=$i"
                }
                response {
                    //title
                    document.a {
                        withClass = "comic-grid-name"
                        titles.addAll(findAll{ return@findAll eachText})
                    }
                    //urls
                    document.a {
                        withClass = "d-block.position-relative"
                        urls.addAll(findAll{ return@findAll eachHref}.map { "https://mangahub.ru$it"} )
                    }
                    //image
                    document.div {
                        withClass = "comic-grid-image"
                        images.addAll(findAll { return@findAll eachAttribute("data-background-image") })
                    }
                }
            }
        }
        for ( i in 0 until urls.size) {
            var description = ""
            var genres =  Genres()
            var chapters =  Chapters()
            var info = Info()
            var typesName =   listOf<String>()
            var typesValue =  listOf<String>()
            var types = ""
            var link = urls[i]
            var rate = ""
            var rateCount = ""
            var views = ""


            skrape(HttpFetcher) {
                request {
                    url = link
                }
                response {
                    // description
                    try {
                        document.div {
                            withClass = "markdown-style.text-expandable-content"
                            description = findFirst { return@findFirst text }
                        }
                    } catch (e: Exception) {
                        description = "Not found"
                    }
                    //genres
                    document.a {
                        withClass = "tag.fw-medium"
                        genres = Genres(title = findAll { return@findAll eachText }.map { it.replace("#","") })
                    }

                    // views
                    document.span {
                        withClass = "ms-2.fw-bold.fs-2.fs-md-5"
                        views = findFirst { return@findFirst text }.replace("К","0").replace(".","")
                    }

                    //types
                    document.div {
                        withClass = "fs-2.text-muted.fw-medium.d-flex.align-items-center"
                        types = findFirst { return@findFirst text }
                    }

                    // info
                    document.div {
                        withClass = "attr-name"
                        typesName = findAll{ return@findAll eachText}
                    }
                    // info
                    document.div {
                        withClass = "attr-value"
                        typesValue = findAll{ return@findAll eachText}
                    }

                    //rate
                    document.span {
                        withClass = "fw-bold.fs-2.fs-lg-5"
                        rate = findFirst{ return@findFirst text }
                        println(rate)
                    }

                    //rate count
                    document.span {
                        withClass = "fw-normal.fs-0.text-muted"
                        rateCount = findFirst { return@findFirst text }
                    }

                    info = Info(
                        name = typesName,
                        value = typesValue
                    )


                }
            }
            skrape(HttpFetcher) {
                request {
                    url = link.replace("title","chapters")
                }
                response {
                    //chapters
                    document.a {
                        try {
                            withClass = "d-inline-flex.ms-2.fs-2.fw-medium.text-reset.min-w-0.flex-lg-grow-1"
                            println(findAll { return@findAll eachHref })
                            chapters = Chapters(
                                title = findAll { return@findAll eachText },
                                url = findAll { return@findAll eachHref }.map { "\"https://mangahub.ru/$it" }
                            )
                        } catch (e: Exception) {

                            withClass = "text-muted.text-center.fw-medium.py-4"
                            chapters = Chapters(title = emptyList(), url = emptyList())
                        }
                    }
                }
            }

            val tempList = types.split(" ")
            println(link)
            println(tempList)
            val type = tempList[0]
            var yearDK = 0
            val year = try {
                tempList[1].toInt()
                tempList[1]
            } catch (e: Exception) {
                yearDK = 1
                ""
            }
            val status = if (yearDK == 0 ) tempList[2] else tempList[1]
            val limitation = if (tempList.size == 4){
                tempList[3]
            } else {
                ""
            }

            list = (
                Manga(
                    id = maxId,
                    title = titles[i],
                    image = images[i],
                    url = urls[i],
                    description = description,
                    genres = genres,
                    types = Types(
                        type = type,
                        year = year,
                        status = status,
                        limitation = limitation
                    ),
                    views = views.toInt(),
                    info = info,
                    chapters = chapters,
                    chaptersCount = chapters.url.size,
                    rate = rate.toDouble(),
                    countRate = rateCount.toInt(),
                )
            )
            mangaRepository.save(list)



            println(list)
            maxId = maxId -  1


        }
        return list
    }

    override fun getManga(countCard: Int, status: String?, page: Int, order: String?, genre: String?): List<MangaLightResponse> {
        return sortQuery(
            order = order,
            genre = genre,
            status = status,
            page = page,
            countCard = countCard
        )

    }

    fun sortQuery(order: String?, genre: String?, status: String?, page: Int, countCard: Int): List<MangaLightResponse> {
        val sort = when(order){
            "popular" -> Sort.by(
                Sort.Order(Sort.Direction.DESC, "rate"),
                Sort.Order(Sort.Direction.DESC, "countRate")
            )
            "views" -> Sort.by(
                Sort.Order(Sort.Direction.DESC, "views")
            )
            else -> null
        }

        val pageable: Pageable = if(sort != null ) PageRequest.of(page, countCard, sort) else PageRequest.of(page, countCard)
        val statePage: Page<Manga> = if(status != null && genre != null){
            mangaRepository.findAll(pageable)
        } else if (status == null && genre != null){
            if(genre != "random") {
                mangaRepository.findByGenres(pageable, genre)
            } else {
                mangaRepository.findByRandom(pageable)
            }
        } else if (status != null) {
            mangaRepository.findByStatus(pageable, status)
        } else {
            mangaRepository.findAll(pageable)
        }
        val light = mutableListOf<MangaLightResponse>()

        statePage.content.forEach {
            light.add(
                MangaLightResponse(
                    id = it.id,
                    title = it.title,
                    image = it.image,
                    url = it.url,
                    rate = it.rate,
                    countRate = it.countRate,
                    description = it.description,
                    countViews = it.views
                )
            )
        }
        return light
    }


    override fun getMangaFromDB(id: Int): Manga {
        return try {
            mangaRepository.findById(id).get()
        } catch (e: Exception) {
            Manga()
        }
    }

    override fun test(): List<String> {
        val driver = setWebDriver("https://mangalib.me/")
        val list = mutableListOf<String>()
        val elems = driver.findElements(By.xpath("//a[@href]"))
        for( i in 0 until elems.size){
            list.add(elems[i].getAttribute("href"))
        }
        println(list.size)
        return list
    }

    override fun readMangaByLink(url: String): List<String> {
        val driver = setWebDriver(url)
        val pagesReader = driver.findElement(By.xpath("//*[@class=\"button reader-pages__label reader-footer__btn\"]")).text
        val pagesCount = pagesReader.replace("Страница 1 / ", "").toInt()

        val pages = mutableListOf<String>()

        for (page in 1 until pagesCount ) {
            driver.get("$url?page=$page")
            val imgs = driver.findElement(By.xpath("//*[@class=\"reader-view__wrap\"]"))
            pages.add(imgs.findElement(By.tagName("img")).getAttribute("src"))
        }

        driver.quit()
        return pages.toList()
    }

    fun setWebDriver(url: String): WebDriver {
        val pathDriver: String = when (getOS()) {
            // Loaded from here https://chromedriver.storage.googleapis.com/index.html?path=101.0.4951.41/
            OS.WINDOWS -> "_win32_101.exe"
            OS.LINUX-> "_linux64_101"
            OS.MAC-> "_mac64_101"
            else -> throw Exception("Unknown operating system!")
        }
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver$pathDriver");
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriver(options)
        try {
            driver.get(url);
        } catch (e: Exception) {
            println(e.message)

            throw Exception(e.localizedMessage)
        }
        return driver
    }


}