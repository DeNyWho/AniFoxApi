package com.example.anifoxapi.service.anime

import com.example.anifoxapi.model.anime.Anime
import com.example.anifoxapi.model.manga.MangaTags
import com.example.anifoxapi.util.OS
import com.example.anifoxapi.util.getOS
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration


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
        println(driver.currentUrl)
        driver.findElements(By.xpath("//*[@class=\"media-chapter__name text-truncate\"]"))
        Thread.sleep(500)
        driver.navigate().to("$url?section=chapters")
        driver.manage().window().maximize()
        (driver as JavascriptExecutor)
            .executeScript("window.scrollTo(0, document.body.scrollHeight)")
        Thread.sleep(2000)
        val chapterName = WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@class=\"media-chapter__name text-truncate\"]")))
        println("CHAPTER NAME = ${chapterName.size}")
        val chapters = mutableListOf<String>()

        chapterName.forEach {
            chapters.add(it.text)
        }
        return Anime(
            title = title,
            image = image,
            description = description,
            tags = tags,
            list = MangaTags(title = listTitleFinal, value = listValueFinal),
            chapters = chapters.toList()
        )

    }


}