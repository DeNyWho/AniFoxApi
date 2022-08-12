package com.example.anifoxapi.controller.anime

import com.example.anifoxapi.jpa.manga.Manga
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.manga.TestMangaResponse
import com.example.anifoxapi.model.responses.ServiceResponse
import com.example.anifoxapi.service.manga.MangaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "MangaApi", description = "All about manga")
@RequestMapping("/api2/manga/")
class MangaParserController {

    @Autowired
    lateinit var service: MangaService

    @GetMapping("search")
    @Operation(summary = "Search manga")
    fun search(
        @RequestParam search: String,
    ): ServiceResponse<MangaLightResponse> {
        return try {
            val data = service.search(query = search)

            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

    @GetMapping("{id}")
    @Operation(summary = "Get detail of manga")
    fun getMangaById(
        @PathVariable id: Int
    ): ServiceResponse<Manga> {
        return try {
            val data = service.getMangaFromDB(id)

            return ServiceResponse(data = listOf(data), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

    @GetMapping("")
    @Operation(summary = "Get manga")
    fun getManga(
        @RequestParam page: Int,
        @RequestParam countCard: Int,
        status: String?,
        order: String?,
        genre: String?
    ): ServiceResponse<MangaLightResponse> {
        return try {
            val data = service.getManga(
                countCard = countCard,
                status = status,
                page = page,
                order = order,
                genre = genre
            )

            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("parser")
    @Operation(summary = "Parse manga and add data to postgreSQL")
    fun parseManga(): ServiceResponse<Long> {
        return try {
            val start = System.currentTimeMillis()
            service.addPopularDataToDB()

            val finish = System.currentTimeMillis()
            val elapsed = finish - start
            println("Время выполнения $elapsed")
            return ServiceResponse(data = listOf(elapsed), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)

        } catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

}