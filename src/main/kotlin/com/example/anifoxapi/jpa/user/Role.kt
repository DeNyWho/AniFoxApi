package com.example.anifoxapi.jpa.user

import lombok.NoArgsConstructor
import javax.persistence.*


@NoArgsConstructor
@Entity
@Table(name = "roles", schema = "public_users")
data class Role (

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name="name", nullable = false)
    val name: String? = null
)
