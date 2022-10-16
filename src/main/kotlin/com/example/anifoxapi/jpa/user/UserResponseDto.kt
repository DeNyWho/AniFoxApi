package com.example.anifoxapi.jpa.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserResponseDto(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = 0,

    @Column(name="username")
    var username: String? = null,

    @Column(name="email")
    var email: String? = null,

    @Column(name="password")
    var password: String? = null,

    @Column(name="enabled")
    var enabled: Boolean = false,

    @Column(name="token")
    var token: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
        schema = "public_users"
    )

    var roles: Collection<Role>? = null,

    @CreatedDate
    @Column(name = "created")
    var created: LocalDateTime? = null,
)

