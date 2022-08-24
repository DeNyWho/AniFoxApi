package com.example.anifoxapi.controller.user

import com.example.anifoxapi.jpa.manga.Manga
import com.example.anifoxapi.jpa.manga.MangaResponseDto
import com.example.anifoxapi.model.manga.MangaLightResponse
import com.example.anifoxapi.model.responses.BasicResponse
import com.example.anifoxapi.model.responses.PageableResponse
import com.example.anifoxapi.model.user.FavouriteDto
import com.example.anifoxapi.service.user.FavouriteService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@Tag(name = "User API", description = "All about user")
@RequestMapping("/api2/user/favourite/")
class UserController {

    @Autowired
    lateinit var favouriteService: FavouriteService

    @PostMapping("add")
    fun addFavourite(@RequestBody dto: FavouriteDto, @RequestParam status: String ): BasicResponse<Void> {
        favouriteService.addFavourite(dto, status)
        return BasicResponse()
    }

    @PostMapping("remove")
    fun removeFromFavourite(@RequestBody dto: FavouriteDto): BasicResponse<Void> {
        favouriteService.removeFavourite(dto)
        return BasicResponse()
    }

    @GetMapping
    fun getFavouriteByUserUuid(
        @RequestParam id: Long,
        @RequestParam(defaultValue = "1") pageNum: @Min(1) Int,
        @RequestParam(defaultValue = "12") pageSize: @Min(1) @Max(500) Int
    ): BasicResponse<PageableResponse<MangaLightResponse>> {
        println("HUCH")
        return BasicResponse(favouriteService.getUserMangaByUserId(id, pageNum, pageSize))
    }

//    @PostMapping("/checkIsFavourite")
//    fun checkIsFavourite(@RequestBody dto: FavouriteDto): BasicResponse<Boolean> {
//        return BasicResponse(favouriteService.checkIsFavourite(dto), true)
//    }


}