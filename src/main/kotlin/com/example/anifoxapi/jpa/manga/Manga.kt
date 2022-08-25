package com.example.anifoxapi.jpa.manga

import com.example.anifoxapi.jpa.user.User
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name="manga")
data class Manga(
    @Id
    val id: Int = 0,
    var title: String = "",
    var image: String = "",
    var url: String = "",
    @Column(columnDefinition = "TEXT")
    var description: String = "",
    @OneToOne(cascade = [CascadeType.ALL])
    var genres: Genres = Genres(),
    @OneToOne(cascade = [CascadeType.ALL])
    var types: Types = Types(),
    @OneToOne(cascade = [CascadeType.ALL])
    var info: Info = Info(),
    @OneToOne(cascade = [CascadeType.ALL])
    var chapters: Chapters = Chapters(),
    val chaptersCount: Int = 0,
    var views: Int = 0,
    var rate: Double = 0.0,
    @Column(name = "count_rate")
    var countRate: Int = 0,

    @ManyToMany(
        fetch = FetchType.EAGER,
        mappedBy = "favouriteManga",
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    ) var users: MutableSet<User> = mutableSetOf(),

    @ManyToMany(
        fetch = FetchType.EAGER,
        mappedBy = "watchingManga",
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    ) var watchMangaUsers: MutableSet<User> = mutableSetOf(),

    @ManyToMany(
        fetch = FetchType.EAGER,
        mappedBy = "completedManga",
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    ) var completedMangaUsers: MutableSet<User> = mutableSetOf(),

    @ManyToMany(
        fetch = FetchType.EAGER,
        mappedBy = "onHoldManga",
        cascade = [CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH]
    ) var onHoldMangaUsers: MutableSet<User> = mutableSetOf(),
){
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
            return false
        }
        val manga = other as Manga
        return id == manga.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
