package com.example.anifoxapi.controller.user

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
@RequestMapping("/api2/user/")
class UserController {

    @Autowired
    lateinit var favouriteService: FavouriteService

    @PostMapping("favourite/add")
    fun addFavourite(@RequestBody dto: FavouriteDto, @RequestParam status: String ): BasicResponse<Void> {
        favouriteService.addFavourite(dto, status)
        return BasicResponse()
    }

    @PostMapping("favourite/remove")
    fun removeFromFavourite(@RequestBody dto: FavouriteDto): BasicResponse<Void> {
        favouriteService.removeFavourite(dto)
        return BasicResponse()
    }

    @GetMapping("favourite/")
    fun getFavouriteByUserUuid(
        @RequestParam id: Long,
        @RequestParam(defaultValue = "1") pageNum: @Min(1) Int,
        @RequestParam(defaultValue = "12") pageSize: @Min(1) @Max(500) Int,
        status: String?
    ): BasicResponse<PageableResponse<MangaLightResponse>> {
        return BasicResponse(favouriteService.getUserMangaByUserId(id, pageNum, pageSize, status))
    }


}