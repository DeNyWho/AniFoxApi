package com.example.anifoxapi.service.manga

import com.example.anifoxapi.jpa.manga.*
import com.example.anifoxapi.model.manga.MangaLightResponse
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
import org.openqa.selenium.interactions.Actions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class MangaService: MangaRep {

    @Autowired
    lateinit var mangaRepository: MangaRepository

    override fun search(query: String): List<MangaLightResponse> {
        val driver = setWebDriver("https://mangalib.me/manga-list")
        driver.get("https://mangalib.me/manga-list?sort=rate&dir=desc&page=1&types[]=1")

        val searchBox = driver.findElement(By.xpath("//input[@class='form__input manga-search__input']"))
        searchBox.click()
        searchBox.sendKeys(query, Keys.ENTER)
        Thread.sleep(500)
        val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
        val data = mutableListOf<MangaLightResponse>()
        for (i in 0 until list.size){
            data.add(
                MangaLightResponse(
                    title = list[i].text.drop(6),
                    image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                    url = list[i].getAttribute("href")
                )
            )
        }
        driver.quit()

        return data
    }

    override fun addPopularDataToDB(): Manga {
        var maxId = 0
        var pageSize = 0
        var urls = mutableListOf<String>()
        var images = mutableListOf<String>()
        var titles = mutableListOf<String>()
        var list = Manga()

        skrape(HttpFetcher) {
            request {
                url = "https://mangahub.ru/explore/genres-is-nor-erotica/sort-is-date?page=1"
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
        for ( i in 1 until pageSize + 1 ) {
            println(i)
            skrape(HttpFetcher) {
                request {
                    url = "https://mangahub.ru/explore/genres-is-nor-erotica/sort-is-date?page=$i"
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
                        genres = Genres(title = findAll { return@findAll eachText })
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

            var tempList = types.split(" ")

            val type = tempList[0]
            val year = tempList[1]
            val status = tempList[2]
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
                    info = info,
                    chapters = chapters
                )
            )
            mangaRepository.save(list)



            println(list)
            maxId = maxId -  1


        }
        return list
    }

    override fun getMangaFromDB(id: Int): Manga {
        try {
            return mangaRepository.findById(id).get()
        } catch (e: Exception) {
            return Manga()
        }
    }

    override fun manga(countPage: Int, status: Int?, countCard: Int?, sort: String?): List<MangaLightResponse>{
        val driver = setWebDriver("https://mangalib.me/manga-list")
        val data = mutableListOf<MangaLightResponse>()
        val newDimension = Dimension(2000, 4000)
        driver.manage().window().size = newDimension
        val newSetDimension = driver.manage().window().size
        val newHeight = newSetDimension.getHeight()
        val newWidth = newSetDimension.getWidth()
        println("Current height: $newHeight")
        println("Current width: $newWidth")

        if(countCard == null) {
            for (i in 0 until countPage) {
                if (status == null) {
                    Thread.sleep(500)
                    driver.get("https://mangalib.me/manga-list?sort=$sort&dir=desc&page=${i + 1}&types[]=1")
                    val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                    for (i in 0 until list.size) {
                        data.add(
                            MangaLightResponse(
                                title = list[i].text.drop(6),
                                image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                                list[i].getAttribute("href")
                            )
                        )
                    }
                } else {
                    Thread.sleep(500)

                    driver.get("https://mangalib.me/manga-list?sort=$sort&dir=desc&page=${i + 1}&status[]=$status&types[]=1")
                    val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                    for (i in 0 until list.size) {
                        data.add(
                            MangaLightResponse(
                                title = list[i].text.drop(6),
                                image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                                list[i].getAttribute("href")
                            )
                        )
                    }
                }
            }
        } else {
            if (status == null) {
                Thread.sleep(500)

                driver.get("https://mangalib.me/manga-list?sort=$sort&dir=desc&page=1&types[]=1")
                val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                for (i in 0 until countCard) {
                    data.add(
                        MangaLightResponse(
                            title = list[i].text.drop(6),
                            image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                            list[i].getAttribute("href")
                        )
                    )
                }
            } else {
                Thread.sleep(500)

                driver.get("https://mangalib.me/manga-list?sort=$sort&dir=desc&page=1&status[]=$status&types[]=1")
                val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                for (i in 0 until countCard) {
                    data.add(
                        MangaLightResponse(
                            title = list[i].text.drop(6),
                            image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                            url = list[i].getAttribute("href"),
                        )
                    )
                }
            }
        }
        driver.quit()


        return data
    }


//    override fun details(url: String): Manga {
//        val driver = setWebDriver(url)
//
//        val description = driver.findElement(By.xpath("//*[@class=\"media-description__text\"]")).text
//        val image = driver.findElement(By.xpath("//meta[@property='og:image']")).getAttribute("content")
//        val title = driver.findElement(By.xpath("//meta[@property='og:title']")).getAttribute("content")
//        val tags = driver.findElement(By.xpath("//*[@class=\"media-tags\"]")).text.replace("\n",",")
//        val listTitle = driver.findElements(By.xpath("//*[@class=\"media-info-list__item\"]"))
//        val listTitleReady = mutableListOf<String>()
//        val listTitleFinal = mutableListOf<String>()
//        val listValueFinal = mutableListOf<String>()
//        val result = mutableListOf<String>()
//
//        listTitle.forEach{
//            listTitleReady.add(it.text.replace("\n",", "))
//        }
//
//        for(i in 0 until listTitleReady.size){
//            result.addAll(listTitleReady[i].split(",").map { it.trim() })
//        }
//
//        for (i in 0 until result.size){
//            if (i % 2 == 0) {
//                listTitleFinal.add(result[i])
//            } else {
//                listValueFinal.add(result[i])
//            }
//        }
//        println(url)
//
//        driver.get("$url?section=chapters")
//        driver.navigate().to("$url?section=chapters")
//        driver.manage().window().maximize()
//
//        (driver as JavascriptExecutor)
//            .executeScript("window.scrollTo(0, document.body.scrollHeight)")
//        Thread.sleep(2000)
//        val chapterName = scrollSmooth(driver)
//
//        val chaptersTitle = mutableListOf<String>()
//        val chaptersUrl = mutableListOf<String>()
//
//        chapterName.forEach {
//            chaptersTitle.add(it.text)
//            chaptersUrl.add(it.findElement(By.tagName("a")).getAttribute("href"))
//        }
//        driver.quit()
//
//        return Manga(
//            title = title,
//            image = image,
//            description = description,
//            genres = tags.split(","),
//            list = MangaTags(title = listTitleFinal, value = listValueFinal),
//            chapters = MangaChapters(title = chaptersTitle.toList(), url = chaptersUrl.toList())
//        )
//    }

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

    fun scrollSmooth(driver: WebDriver): List<WebElement> {
        val newDimension = Dimension(20000, 90000)
        driver.manage().window().size = newDimension
        val newSetDimension = driver.manage().window().size
        val newHeight = newSetDimension.getHeight()
        val newWidth = newSetDimension.getWidth()
        println("Current height: $newHeight")
        println("Current width: $newWidth")
        val action = Actions(driver)
        action.sendKeys(Keys.PAGE_DOWN).build().perform()

//            (driver as JavascriptExecutor).executeScript("window.scrollBy(0,1000)", "")
            driver.findElements(By.xpath("//*[@class=\"media-chapter__name text-truncate\"]"))
        val list = driver.findElements(By.xpath("//*[@class=\"media-chapter__name text-truncate\"]"))
        return list.toList()
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