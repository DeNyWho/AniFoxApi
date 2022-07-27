package com.example.anifoxapi.controller.anime

import com.example.anifoxapi.model.anime.Anime
import com.example.anifoxapi.model.responses.ServiceResponse
import com.example.anifoxapi.service.anime.AnimeService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api2/")
class AnimeParserController {

    @Autowired
    lateinit var service: AnimeService


    @GetMapping("search")
    @Operation(summary = "Searcher")
    fun search(
        @RequestParam search: String,
    ): ServiceResponse<Anime> {
        val data = service.search(query = search)
        println("data = $data")
        return ServiceResponse(data = data, status = HttpStatus.OK)
    }

//    @GetMapping()
//    @Operation(summary = "Get new popular manga")
//    fun getPopularManga(request: HttpServletRequest): ServiceResponse<Anime> {
//        return try {
//            val data = service
//
//        } catch (e: ChangeSetPersister.NotFoundException) {
//            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
//        } catch (e: Exception) {
//            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
//        }
//    }

}