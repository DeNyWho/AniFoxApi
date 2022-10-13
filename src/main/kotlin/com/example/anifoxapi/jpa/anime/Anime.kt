package com.example.anifoxapi.jpa.anime

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="anime", schema = "Anime")
data class Anime(
    @Id
    val id: Int = 0,
    var title: String = "",
    var image: String = "",
    var url: String = "",

)