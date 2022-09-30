package com.example.anifoxapi.service.manga

import com.example.anifoxapi.jpa.manga.*
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.support.ChapterSupport
import com.example.anifoxapi.repository.manga.GenreRepository
import com.example.anifoxapi.repository.manga.MangaRep
import com.example.anifoxapi.repository.manga.MangaRepository
import com.example.anifoxapi.util.OS
import com.example.anifoxapi.util.getOS
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import it.skrape.selects.eachAttribute
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.*
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

    fun toDto(manga: Manga, chapters: Chapters): MangaResponseDto {
        return MangaResponseDto(
            id = manga.id,
            title = manga.title,
            image = manga.image,
            url = manga.url,
            description = manga.description,
            genres = manga.genres,
            types = manga.types,
            info = manga.info,
            chapters = chapters,
            chaptersCount = manga.chaptersCount,
            views = manga.views,
            rate = manga.rate,
            countRate = manga.countRate,
        )
    }

    @Autowired
    lateinit var mangaRepository: MangaRepository

    @Autowired
    lateinit var genreRepository: GenreRepository

    private var pagesBoolean = false

    override fun search(query: String): List<MangaLightResponse?> {

        val light = mutableListOf<MangaLightResponse>()
        val manga = mangaRepository.findByTitleSearch(query)

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

    override fun genres(): List<String> {
        val v = genreRepository.wtf()
        val temp = mutableListOf<String>()
        v.forEach {
            it.title.forEach {v ->
                if (!v.contains("#")) {
                    temp.add(v)
                }
            }
        }
        return temp.distinct()
    }

    override fun similarManga(id: Int): List<MangaLightResponse> {
        val light = mutableListOf<MangaLightResponse>()

        val list = mangaRepository.findByLikeManga(id.toLong())
        val mangas = mutableListOf<Manga>()

        for(i in 0 until list.title.size){
            try {
                mangas.addAll(mangaRepository.findByTitle(list.title[i]))
            } catch (e: Exception){

            }
        }

        if(mangas.size > 0){
            mangas.forEach {
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
        }

        return light.toList()
    }

    override fun chaptersManga(id: Int): Chapters {
        val manga = mangaRepository.findById(id)
        val chapters = manga.get().chapters
        return chapters
    }

    override fun linkedManga(id: Int): List<MangaLightResponse> {
        val light = mutableListOf<MangaLightResponse>()

        val list = mangaRepository.findByLinkedManga(id.toLong())
        println("FDSFSD = $list")
        val mangas = mutableListOf<Manga>()

        for(i in 0 until list.title.size){
            try {
                val tempList = mangaRepository.findByTitle(list.title[i])
                if(tempList.size > 1 ){
                    tempList.forEach {
                        if(it.id != id){
                            mangas.add(it)
                        }
                    }
                } else {
                    mangas.addAll(tempList)
                }
            } catch (e: Exception){
                println("Mangas = ${e.message}")
            }
        }
        println("Mangas = ${mangas.size}")

        if(mangas.size > 0){
            mangas.forEach {
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
        }

        return light.toList()
    }

    override fun addDataToDB(): Manga {
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

        for ( i in 1 until pageSize) {
            println(i)
            pagesBoolean = false
            while (!pagesBoolean) {
                println("This try Pages")
                try {
                    skrape(HttpFetcher) {
                        request {
                            url =
                                "https://mangahub.ru/explore/type-is-nor-comix/genres-is-nor-erotica/sort-is-date?page=$i"
                            timeout = 400_000
                        }
                        response {
                            //title
                            document.div {
                                withClass = "text-line-clamp.mt-2"
                                titles.addAll(findAll { return@findAll eachText })
                            }
                            //urls
                            document.a {
                                withClass = "d-block.rounded.fast-view-layer"
                                urls.addAll(findAll { return@findAll eachHref }.map { "https://mangahub.ru$it" })
                            }
                            //image
                            document.div {
                                withClass = "comic-grid-image"
                                images.addAll(findAll { return@findAll eachAttribute("data-background-image") }.map { "https://mangahub.ru$it" })
                            }
                        }
                    }
                    pagesBoolean = true
                } catch (e: Exception) {
                    pagesBoolean = false
                }
            }
        }
        for ( i in 0 until urls.size) {
            println(i)
            pagesBoolean = false
            while (!pagesBoolean) {

                try {
                    var description = ""
                    var genres = Genres()
                    var chapters = Chapters()
                    var urlsSlides = listOf<String>()
                    var urlsTitles = listOf<String>()
                    var urlsDates = listOf<String>()
                    var info = Info()
                    var typesName = listOf<String>()
                    var typesValue = listOf<String>()
                    var types = ""
                    var link = urls[i]
                    var rate = ""
                    var rateCount = ""
                    var views = ""
                    var countLikes = 0


                    skrape(HttpFetcher) {
                        request {
                            url = link
                            timeout = 400_000
                        }
                        response {
                            // description
                            try {
                                val b = mutableListOf<String>()
                                document.div {
                                    withClass = "detail-section-header"
                                    b.addAll(findAll { return@findAll eachText })
                                }
                                countLikes = b[3].takeLast(2).toInt()
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
                                val b = findAll { return@findAll eachText }
                                val c = mutableListOf<String>()

                                b.forEach {
                                    if (it.contains("/")) {
                                        c.add(it.split("/")[0].dropLast(1))
                                    } else {
                                        c.add(it)
                                    }
                                }
                                genres = Genres(
                                    id = maxId.toLong(),
                                    title = c
                                )
                            }

                            // views
                            document.span {
                                withClass = "ms-2.fw-bold.fs-2.fs-md-5"
                                views = findFirst { return@findFirst text }.replace("К", "0").replace(".", "")
                            }

                            //types
                            document.div {
                                withClass = "fs-2.text-muted.fw-medium.d-flex.align-items-center"
                                types = findFirst { return@findFirst text }
                            }

                            // info
                            document.div {
                                withClass = "attr-name"
                                typesName = findAll { return@findAll eachText }
                            }
                            // info
                            document.div {
                                withClass = "attr-value"
                                typesValue = findAll { return@findAll eachText }
                            }

                            //rate
                            document.span {
                                withClass = "rating-star-rate"
                                rate = findFirst { return@findFirst text }
                                println(rate)
                            }

                            //rate count
                            document.span {
                                withClass = "rating-star-votes"
                                rateCount = findFirst { return@findFirst text }
                            }

                            info = Info(
                                id = maxId.toLong(),
                                name = typesName,
                                value = typesValue
                            )


                        }
                    }

                    // chapters page

                    skrape(HttpFetcher) {
                        request {
                            url = link.replace("title", "chapters")
                            timeout = 400_000
                        }
                        response {
                            try {
                                //chapters
                                document.a {
                                    try {
                                        withClass =
                                            "d-inline-flex.ms-2.fs-2.fw-medium.text-reset.min-w-0.flex-lg-grow-1"
                                        urlsSlides =
                                            findAll { return@findAll eachHref }.map { "https://mangahub.ru$it?page=1" }
                                        urlsTitles = findAll { return@findAll eachText }
                                    } catch (e: Exception) {
                                        withClass = "text-muted.text-center.fw-medium.py-4"
                                        urlsSlides = emptyList()
                                    }
                                }
                                document.div {
                                    withClass = "detail-chapter-date.ms-2.text-muted"
                                    urlsDates = findAll { return@findAll eachText }
                                }
                            } catch (e: Exception) {

                            }
                        }
                    }

                    // like page
                    val temping = mutableListOf<String>()
                    val linked = mutableListOf<String>()

                    skrape(HttpFetcher) {
                        request {
                            url = "$link/like"
                            timeout = 400_000
                        }
                        response {

                            val elements = document.allElements.filter { it.className.contains("scroller-item me-3") }
                            val c = mutableListOf<String>()
                            val d = mutableListOf<DocElement>()

                            elements.forEach {
                                c.addAll(it.div {
                                    withClass = "text-line-clamp.mt-2"
                                    findAll { return@findAll eachText }
                                }
                                )
                                d.addAll(it.allElements.filter { it.className == "mt-2" })
                            }


                            for (r in 0 until countLikes) {
                                temping.add(c[r])
                            }

                            linked.addAll(c.takeLast(d.size))
                        }
                    }

                    val tempList = types.split(" ")

                    val type = tempList[0]
                    var yearDK = 0
                    val year = try {
                        tempList[1].toInt()
                        tempList[1]
                    } catch (e: Exception) {
                        yearDK = 1
                        ""
                    }
                    val status = try {
                        if (yearDK == 0) tempList[2] else tempList[1]
                    } catch (e: Exception) {
                        "онгоинг"
                    }
                    val limitation = if (tempList.size == 4) {
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
                                    id = maxId.toLong(),
                                    type = type,
                                    year = year,
                                    status = status,
                                    limitation = limitation
                                ),
                                views = views.toInt(),
                                info = info,
                                chapters = Chapters(
                                    id = maxId.toLong(),
                                    title = urlsTitles,
                                    url = urlsSlides,
                                    date = urlsDates
                                ),
                                chaptersCount = urlsTitles.size,
                                rate = rate.toDouble(),
                                countRate = rateCount.toInt(),
                                likeManga = LikeManga(manga_id = maxId.toLong(), title = temping),
                                linked = Linked(id = maxId.toLong(), title = linked)
                            )
                            )

                    mangaRepository.save(list)

                    maxId = maxId - 1
                    pagesBoolean = true
                } catch (e: Exception){
                    pagesBoolean = false
                }
            }


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
            mangaRepository.findByStatusAndGenre(pageable, status, genre)
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


    override fun getMangaFromDB(id: Int): MangaResponseDto {
        return try {
            val manga = mangaRepository.findById(id)
            val titles = manga.get().chapters.title
            val urls = manga.get().chapters.url
            val temp = mutableListOf<String>()
            urls.forEach {
                temp.addAll(it.split("/vol").filter { it.contains("page") })
            }
            val tempUrl = urls[0].split("/vol").filter { !it.contains("page") }[0]
            val x = mutableListOf<String>()
            temp.forEach { x.addAll(it.split("?page=1").filter { it.isNotEmpty() }) }
            val z = mutableListOf<String>()
            x.forEach {
                z.addAll(it.split("/").filter { it.isNotEmpty() })
            }
            val v = mutableListOf<ChapterSupport>()
            var k = 1
            var c = 0
            for(i in 0 until z.size){
                try {
                    v.add(
                        ChapterSupport(
                            vol = z[c].toInt(),
                            number = z[k].toInt()
                        )
                    )
                    c = c + 2
                    k = k + 2
                } catch (e: Exception) {}
            }
            val finalTemp = v.sortedWith(compareBy<ChapterSupport> { it.vol }.thenBy { it.number }.reversed())
            val final = mutableListOf<String>()

            for( i in finalTemp.indices){
                final.add("$tempUrl/vol${finalTemp[i].vol}/${finalTemp[i].number}?page=1")
            }

            val date = manga.get().chapters.date
            val chapters = Chapters(
                id = id.toLong(),
                title = titles,
                url = final,
                date = date
            )
            return toDto(manga.get(), chapters)
        } catch (e: Exception) {
            MangaResponseDto()
        }
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
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
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