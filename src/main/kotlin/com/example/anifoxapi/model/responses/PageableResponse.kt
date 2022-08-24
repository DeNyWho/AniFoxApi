package com.example.anifoxapi.model.responses

import com.example.anifoxapi.model.PageableData

class PageableResponse<T>(
    var data: Collection<T>? = null,
    var pageable: PageableData? = null
)