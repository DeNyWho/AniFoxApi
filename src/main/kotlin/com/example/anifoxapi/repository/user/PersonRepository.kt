package com.example.anifoxapi.repository.user

import com.example.anifoxapi.jpa.user.Person
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository: CrudRepository<Person, Long>