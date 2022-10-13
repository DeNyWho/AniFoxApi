package com.example.anifoxapi.controller.anime

import com.example.anifoxapi.model.responses.ServiceResponse
import com.example.anifoxapi.service.anime.AnimeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "AnimeApi", description = "All about anime")
@RequestMapping("/api2/anime/")
class AnimeController {

    @Autowired
    lateinit var service: AnimeService

    @GetMapping("parser")
    @Operation(summary = "Parse anime and add data to postgreSQL")
    fun parseAnime(): ServiceResponse<Long> {
        return try {
            val start = System.currentTimeMillis()
            service.addDataToDB()

            val finish = System.currentTimeMillis()
            val elapsed = finish - start
            println("Время выполнения $elapsed")
            return ServiceResponse(data = listOf(elapsed), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

}