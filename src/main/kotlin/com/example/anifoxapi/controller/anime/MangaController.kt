package com.example.anifoxapi.controller.anime

import com.example.anifoxapi.jpa.manga.Genres
import com.example.anifoxapi.jpa.manga.MangaResponseDto
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.responses.ServiceResponse
import com.example.anifoxapi.service.manga.MangaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min


@RestController
@Tag(name = "MangaApi", description = "All about manga")
@RequestMapping("/api2/manga/")
class MangaController {

    @Autowired
    lateinit var service: MangaService

    @GetMapping("search")
    @Operation(summary = "Search manga")
    fun search(
        @RequestParam query: String,
    ): ServiceResponse<MangaLightResponse?> {
        return try {
            val data = service.search(query = query)
            return if (data.isEmpty()){
                ServiceResponse(data = null, status = HttpStatus.BAD_REQUEST, message = "Not Found")
            } else ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("genres")
    @Operation(summary = "get manga genres")
    fun genres(): ServiceResponse<String?> {
        return try {
            val data = service.genres()
            return if (data.isEmpty()){
                ServiceResponse(data = null, status = HttpStatus.BAD_REQUEST, message = "Not Found")
            } else ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }



    @GetMapping("{id}")
    @Operation(summary = "Get detail of manga")
    fun getMangaById(
        @PathVariable id: Int
    ): ServiceResponse<MangaResponseDto> {
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
        @RequestParam(defaultValue = "1") pageNum: @Min(1) Int,
        @RequestParam(defaultValue = "12") pageSize: @Min(1) @Max(500) Int,
        status: String?,
        order: String?,
        genre: String?
    ): ServiceResponse<MangaLightResponse> {
        return try {
            val data = service.getManga(
                countCard = pageSize,
                status = status,
                page = pageNum,
                order = order,
                genre = genre
            )

            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("similar/{id}")
    @Operation(summary = "Get similar of manga")
    fun similarManga(
            @PathVariable id: Int
    ): ServiceResponse<MangaLightResponse> {
        return try {

            val data = service.similarManga(id = id)

            return ServiceResponse(data = data, status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
    }

    @GetMapping("linked/{id}")
    @Operation(summary = "Get linked of manga")
    fun linkedManga(
            @PathVariable id: Int
    ): ServiceResponse<MangaLightResponse> {
        return try {

            val data = service.linkedManga(id = id)

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
            service.addDataToDB()

            val finish = System.currentTimeMillis()
            val elapsed = finish - start
            println("Время выполнения $elapsed")
            return ServiceResponse(data = listOf(elapsed), status = HttpStatus.OK)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ServiceResponse(status = HttpStatus.NOT_FOUND, message = e.message!!)
        }
        catch (e: Exception) {
            ServiceResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, message = e.message!!)
        }
    }

}