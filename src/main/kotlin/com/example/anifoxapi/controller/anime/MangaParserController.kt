package com.example.anifoxapi.controller.anime

import com.example.anifoxapi.model.anime.Anime
import com.example.anifoxapi.model.responses.ServiceResponse
import com.example.anifoxapi.service.anime.MangaService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api2/manga/")
class MangaParserController {

    @Autowired
    lateinit var service: MangaService


    @GetMapping("search")
    @Operation(summary = "Search anime")
    fun search(
        @RequestParam search: String,
    ): ServiceResponse<Anime> {
        return try {
            val data = service.search(query = search)
            println("data = $data")
            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

    @GetMapping("popular")
    @Operation(summary = "Get new popular manga")
    fun getPopularManga(
        @RequestParam countPage: Int,
        @RequestParam( required = false) status: Int?,
        @RequestParam( required = false) countCard: Int?
    ): ServiceResponse<Anime> {
        return try {
            val data = service.popular(countPage, status, countCard)

            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

}