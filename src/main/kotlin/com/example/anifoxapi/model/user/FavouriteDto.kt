package com.example.anifoxapi.model.user

import org.jetbrains.annotations.NotNull

data class FavouriteDto(
    @NotNull var token: String,
    @NotNull var mangaId: Int
)