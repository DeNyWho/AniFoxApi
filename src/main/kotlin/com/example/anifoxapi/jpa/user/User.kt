package com.example.anifoxapi.jpa.user

import com.example.anifoxapi.jpa.manga.Manga
import org.hibernate.Hibernate
import org.springframework.data.annotation.CreatedDate
import org.springframework.util.CollectionUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
data class User (

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
                inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
        )

        var roles: Collection<Role>? = null,

        @CreatedDate
        @Column(name = "created")
        var created: LocalDateTime? = null,

        @ManyToMany(
                fetch = FetchType.EAGER,
                cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
        )
        @JoinColumn(name = "FavouriteManga")
        @JoinTable(
                name = "Favourite_User_Manga",
                joinColumns =  [JoinColumn(name = "favourite_id")],
                inverseJoinColumns = [JoinColumn(name = "like_id")]
        )
        var favouriteManga: MutableSet<Manga> = mutableSetOf(),
        @ManyToMany(
                fetch = FetchType.EAGER,
                cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
        )
        @JoinColumn(name = "WatchingManga")
        @JoinTable(
                name = "Watch_User_Manga",
                joinColumns =  [JoinColumn(name = "favourite_id")],
                inverseJoinColumns = [JoinColumn(name = "watching_id")]
        )
        var watchingManga: MutableSet<Manga> = mutableSetOf(),
        @ManyToMany(
                fetch = FetchType.EAGER,
                cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
        )
        @JoinColumn(name = "CompletedManga")
        @JoinTable(
                name = "Completed_User_Manga",
                joinColumns =  [JoinColumn(name = "favourite_id")],
                inverseJoinColumns = [JoinColumn(name = "completed_id")]
        )
        var completedManga: MutableSet<Manga> = mutableSetOf(),
        @ManyToMany(
                fetch = FetchType.EAGER,
                cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
        )
        @JoinColumn(name = "OnHoldManga")
        @JoinTable(
                name = "OnHold_User_Manga",
                joinColumns =  [JoinColumn(name = "favourite_id")],
                inverseJoinColumns = [JoinColumn(name = "hold_id")]
        )
        var onHoldManga: MutableSet<Manga> = mutableSetOf(),
){
        fun addToFavourite(manga: Manga): User {
                favouriteManga.add(manga)
                return this
        }
        fun addToWatching(manga: Manga): User {
                watchingManga.add(manga)
                return this
        }
        fun addToCompleted(manga: Manga): User {
                completedManga.add(manga)
                return this
        }
        fun addToOnHold(manga: Manga): User {
                onHoldManga.add(manga)
                return this
        }

        fun removeFromFavourite(manga: Manga): User {
                if (CollectionUtils.isEmpty(favouriteManga)) {
                        return this
                }
                favouriteManga.remove(manga)
                watchingManga.remove(manga)
                completedManga.remove(manga)
                onHoldManga.remove(manga)
                return this
        }
        override fun equals(other: Any?): Boolean {
                if (this === other) {
                        return true
                }
                if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
                        return false
                }
                val user = other as User
                return id != null && id == user.id
        }

        override fun hashCode(): Int {
                return javaClass.hashCode()
        }
}
