package com.example.anifoxapi.service.anime

import com.example.anifoxapi.model.anime.Anime
import com.example.anifoxapi.util.OS
import com.example.anifoxapi.util.getOS
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
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
                    image = list[i].getCssValue("background-image").drop(5).dropLast(2)
                )
            )
        }
        return data
    }

    fun popular(countPage: Int): List<Anime>{
        val driver = setWebDriver("https://mangalib.me/manga-list")
        val data = mutableListOf<Anime>()

        for (i in 0 until countPage) {
            driver.get("https://mangalib.me/manga-list?sort=rate&dir=desc&page=${i+1}&types[]=1")
            Thread.sleep(500)
            val list = driver.findElements(By.xpath("//*[@class=\"media-card\"]"))
            for (i in 0 until list.size) {
                data.add(
                    Anime(
                        title = list[i].text.drop(6),
                        image = list[i].getCssValue("background-image").drop(5).dropLast(2)
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


}