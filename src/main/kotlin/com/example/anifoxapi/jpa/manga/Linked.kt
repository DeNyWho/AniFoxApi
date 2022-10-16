package com.example.anifoxapi.jpa.manga

import javax.persistence.*

@Entity
@Table(name="linked", schema = "manga")
data class Linked (
    @Id
    val id: Long? = 0,

    @ElementCollection
    @JoinTable(schema = "manga", name = "linked_title")
    val title: List<String> = ArrayList<String>(),
)