package com.example.anifoxapi.repository.user

import com.example.anifoxapi.jpa.user.User


interface EmailRepository {
    fun sendSimpleMessage(to: String,
                          subject: String,
                          text: String)

    fun sendSimpleMessageUsingTemplate(to: String,
                                       subject: String,
                                       template: String,
                                       params:MutableMap<String, Any>)

    fun sendMessageWithAttachment(to: String,
                                  subject: String,
                                  text: String,
                                  pathToAttachment: String)

    fun sendHtmlMessage(to: String,
                        subject: String,
                        htmlMsg: String)

    fun sendRegistrationConfirmationEmail(user: User)
}