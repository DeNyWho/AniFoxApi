package com.example.anifoxapi.jpa.user

import java.sql.Date
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "verification_token", schema = "public_users")
data class VerificationToken(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = 0,

    @Column(name = "token")
    var token: String? = null,

    @Column(name = "expiry_date")
    val expiryDate: Date? = null,

    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @JoinColumn(nullable = false, name = "user_id")
    @JoinTable(schema = "public_users", name = "verification_user")
    val user: User? = null
) {
    constructor(token: String?, user: User) : this(0, token, calculateExpiryDate(1440), user)
}

private fun calculateExpiryDate(expiryTimeInMinutes: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = Timestamp(cal.time.time)
    cal.add(Calendar.MINUTE, expiryTimeInMinutes)
    return Date(cal.time.time)
}