package com.example.anifoxapi.model.user

import org.jetbrains.annotations.NotNull

data class FavouriteDto(
    @NotNull var userId: Long,
    @NotNull var mangaId: Int
)