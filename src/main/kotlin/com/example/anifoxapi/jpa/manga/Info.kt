package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="info", schema = "manga")
data class Info(
    @Id
    val id: Long? = 0,
    @Column(columnDefinition = "TEXT")
    @ElementCollection
    @JoinTable(schema = "manga", name = "info_name")
    val name: List<String> = ArrayList<String>(),

    @Column(columnDefinition = "TEXT")
    @ElementCollection
    @JoinTable(schema = "manga", name = "info_value")
    val value: List<String> = ArrayList<String>(),
)