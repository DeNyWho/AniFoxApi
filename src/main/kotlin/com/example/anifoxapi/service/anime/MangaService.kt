package com.example.anifoxapi.service.anime

import com.example.anifoxapi.model.anime.Anime
import com.example.anifoxapi.model.manga.MangaChapters
import com.example.anifoxapi.model.manga.MangaTags
import com.example.anifoxapi.model.manga.Pages
import com.example.anifoxapi.util.OS
import com.example.anifoxapi.util.getOS
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service


@Service
class MangaService {

    fun search(query: String): List<Anime> {
        val driver = setWebDriver("https://mangalib.me/manga-list")
        driver.get("https://mangalib.me/manga-list?sort=rate&dir=desc&page=1&types[]=1")

        val searchBox = driver.findElement(By.xpath("//input[@class='form__input manga-search__input']"))
        searchBox.click()
        searchBox.sendKeys(query, Keys.ENTER)
        Thread.sleep(500)
        val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
        val data = mutableListOf<Anime>()
        for (i in 0 until list.size){
            data.add(
                Anime(
                    title = list[i].text.drop(6),
                    image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                    url = list[i].getAttribute("href")
                )
            )
        }
        return data
    }

    fun popular(countPage: Int, status: Int?, countCard: Int?): List<Anime>{
        val driver = setWebDriver("https://mangalib.me/manga-list")
        val data = mutableListOf<Anime>()

        if(countCard == null) {
            for (i in 0 until countPage) {
                if (status == null) {
                    driver.get("https://mangalib.me/manga-list?sort=rate&dir=desc&page=${i + 1}&types[]=1")
                    Thread.sleep(500)
                    val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                    for (i in 0 until list.size) {
                        data.add(
                            Anime(
                                title = list[i].text.drop(6),
                                image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                            )
                        )
                    }
                } else {
                    driver.get("https://mangalib.me/manga-list?sort=rate&dir=desc&page=${i + 1}&status[]=$status&types[]=1")
                    Thread.sleep(500)
                    val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                    for (i in 0 until list.size) {
                        data.add(
                            Anime(
                                title = list[i].text.drop(6),
                                image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                            )
                        )
                    }
                }
            }
        } else {
            if (status == null) {
                driver.get("https://mangalib.me/manga-list?sort=rate&dir=desc&page=1&types[]=1")
                Thread.sleep(500)
                val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                for (i in 0 until countCard) {
                    data.add(
                        Anime(
                            title = list[i].text.drop(6),
                            image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                        )
                    )
                }
            } else {
                driver.get("https://mangalib.me/manga-list?sort=rate&dir=desc&page=1&status[]=$status&types[]=1")
                Thread.sleep(500)
                val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                for (i in 0 until countCard) {
                    data.add(
                        Anime(
                            title = list[i].text.drop(6),
                            image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                            url = list[i].getAttribute("href"),
                        )
                    )
                }
            }
        }
        return data
    }

    fun newUpdate(countPage: Int, countCard: Int?): List<Anime> {
        val driver = setWebDriver("https://mangalib.me/manga-list")
        val data = mutableListOf<Anime>()

        if(countCard == null) {
            for (i in 0 until countPage) {
                driver.get("https://mangalib.me/manga-list?sort=last_chapter_at&dir=desc&page=$countPage&types[]=1")
                Thread.sleep(500)
                val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                for (i in 0 until list.size) {
                    data.add(
                        Anime(
                            title = list[i].text.drop(6),
                            image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                            url = list[i].getAttribute("href"),
                        )
                    )
                }
            }
        }
        else {
            driver.get("https://mangalib.me/manga-list?sort=last_chapter_at&dir=desc&page=$countPage&types[]=1")
            Thread.sleep(500)
            val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
            for (i in 0 until countCard) {
                data.add(
                    Anime(
                        title = list[i].text.drop(6),
                        image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                        url = list[i].getAttribute("href"),
                    )
                )
            }
        }

        return data
    }

    fun views(countPage: Int, countCard: Int?): List<Anime> {
        val driver = setWebDriver("https://mangalib.me/manga-list")
        val data = mutableListOf<Anime>()

        if(countCard == null) {
            for (i in 0 until countPage) {
                driver.get("https://mangalib.me/manga-list?sort=views&dir=desc&page=$countPage&types[]=1")
                Thread.sleep(500)
                val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
                for (i in 0 until list.size) {
                    data.add(
                        Anime(
                            title = list[i].text.drop(6),
                            image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                            url = list[i].getAttribute("href"),
                        )
                    )
                }
            }
        }
        else {
            driver.get("https://mangalib.me/manga-list?sort=views&dir=desc&page=$countPage&types[]=1")
            Thread.sleep(500)
            val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
            for (i in 0 until countCard) {
                data.add(
                    Anime(
                        title = list[i].text.drop(6),
                        image = list[i].getCssValue("background-image").drop(5).dropLast(2),
                        url = list[i].getAttribute("href"),
                    )
                )
            }
        }

        return data
    }

    fun details(url: String): Anime {
        val driver = setWebDriver(url)

        val description = driver.findElement(By.xpath("//*[@class=\"media-description__text\"]")).text
        val image = driver.findElement(By.xpath("//meta[@property='og:image']")).getAttribute("content")
        val title = driver.findElement(By.xpath("//meta[@property='og:title']")).getAttribute("content")
        val tags = driver.findElement(By.xpath("//*[@class=\"media-tags\"]")).text.replace("\n",", ")
        val listTitle = driver.findElements(By.xpath("//*[@class=\"media-info-list__item\"]"))
        val listTitleReady = mutableListOf<String>()
        val listTitleFinal = mutableListOf<String>()
        val listValueFinal = mutableListOf<String>()
        val result = mutableListOf<String>()

        listTitle.forEach{
            listTitleReady.add(it.text.replace("\n",", "))
        }

        for(i in 0 until listTitleReady.size){
            result.addAll(listTitleReady[i].split(",").map { it.trim() })
        }

        for (i in 0 until result.size){
            if (i % 2 == 0) {
                listTitleFinal.add(result[i])
            } else {
                listValueFinal.add(result[i])
            }
        }
        println(url)

        driver.get("$url?section=chapters")
        driver.navigate().to("$url?section=chapters")
        driver.manage().window().maximize()

        (driver as JavascriptExecutor)
            .executeScript("window.scrollTo(0, document.body.scrollHeight)")
        Thread.sleep(2000)
        val chapterName = scrollSmooth(driver)

        val chaptersTitle = mutableListOf<String>()
        val chaptersUrl = mutableListOf<String>()

        chapterName.forEach {
            chaptersTitle.add(it.text)
            chaptersUrl.add(it.findElement(By.tagName("a")).getAttribute("href"))
        }

        return Anime(
            title = title,
            image = image,
            description = description,
            tags = tags,
            list = MangaTags(title = listTitleFinal, value = listValueFinal),
            chapters = MangaChapters(title = chaptersTitle.toList(), url = chaptersUrl.toList())
        )
    }

    fun readMangaByLink(url: String): List<String> {
        val driver = setWebDriver(url)
        val pagesReader = driver.findElement(By.xpath("//*[@class=\"button reader-pages__label reader-footer__btn\"]")).text
        val pagesCount = pagesReader.replace("Страница 1 / ", "").toInt()

        val pages = mutableListOf<String>()

        for (page in 1 until pagesCount + 1 ) {
            driver.get("$url?page=$page")
            val imgs = driver.findElement(By.xpath("//*[@class=\"reader-view__wrap\"]"))
            pages.add(imgs.findElement(By.tagName("img")).getAttribute("src"))
        }

        return pages.toList()
    }

    fun scrollSmooth(driver: WebDriver): List<WebElement> {
        val newDimension = Dimension(2000, 40000)
        driver.manage().window().size = newDimension
        val newSetDimension = driver.manage().window().size
        val newHeight = newSetDimension.getHeight()
        val newWidth = newSetDimension.getWidth()
        println("Current height: $newHeight")
        println("Current width: $newWidth")
        for (i in 0..999) {
            (driver as JavascriptExecutor).executeScript("window.scrollBy(0,1000)", "")
            driver.findElements(By.xpath("//*[@class=\"media-chapter__name text-truncate\"]"))
        }
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