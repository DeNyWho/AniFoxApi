package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="info")
data class Info(
    @Id
    val id: Long? = 0,
    @Column(columnDefinition = "TEXT")
    @ElementCollection
    val name: List<String> = ArrayList<String>(),

    @Column(columnDefinition = "TEXT")
    @ElementCollection
    val value: List<String> = ArrayList<String>(),
)