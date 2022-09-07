package com.example.anifoxapi.service.user

import com.example.anifoxapi.jpa.user.User
import com.example.anifoxapi.repository.user.EmailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.io.File
import javax.mail.MessagingException

@Service
class EmailService: EmailRepository {

    @Value("\${spring.mail.username}")
    lateinit var sender: String

    @Value("\${host.url}")
    lateinit var hostUrl: String

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var environment: Environment

    @Autowired
    var emailSender: JavaMailSender? = null

    @Autowired
    lateinit var templateEngine: SpringTemplateEngine

    override fun sendSimpleMessage(to: String, subject: String, text: String) {
        try {
            val message = SimpleMailMessage()
            message.setTo(to)
            message.setFrom(sender)
            message.setSubject(subject)
            message.setText(text)

            emailSender!!.send(message)
        } catch (exception: MailException) {
            exception.printStackTrace()
        }

    }

    override fun sendSimpleMessageUsingTemplate(to: String,
                                                subject: String,
                                                template: String,
                                                params:MutableMap<String, Any>) {
        val message = emailSender!!.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "utf-8")
        var context: Context = Context()
        context.setVariables(params)
        val html: String = templateEngine.process(template, context)

        helper.setTo(to)
        helper.setFrom(sender)
        helper.setText(html, true)
        helper.setSubject(subject)

        emailSender!!.send(message)
    }

    override fun sendMessageWithAttachment(to: String,
                                           subject: String,
                                           text: String,
                                           pathToAttachment: String) {
        try {
            val message = emailSender!!.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            helper.setTo(to)
            helper.setFrom(sender)
            helper.setSubject(subject)
            helper.setText(text)

            val file = FileSystemResource(File(pathToAttachment))
            helper.addAttachment("Invoice", file)

            emailSender!!.send(message)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }

    }

    override fun sendHtmlMessage(to: String, subject: String, htmlMsg: String) {
        try {
            val message = emailSender!!.createMimeMessage()
            message.setContent(htmlMsg, "text/html")

            val helper = MimeMessageHelper(message, false, "utf-8")

            helper.setTo(to)
            helper.setFrom(sender)
            helper.setSubject(subject)

            emailSender!!.send(message)
        } catch (exception: MailException) {
            exception.printStackTrace()
        }
    }

    override fun sendHelloMessage(user: User, password: String): String{
        return try {
            val msg = "<b><p>Registration Success</p></b><p>Great, you have successfully registered a AniFox account.</p><p>You can log in your account with the email: <b>${user.email}</b> and password: <b>$password</b>"
            user.email?.let { sendHtmlMessage(user.email!!, "AniFox Company: Hello", msg) }
            "Message has been sent"
        } catch (e: Exception){
            "${e.message}"
        }
    }

    override fun sendRegistrationConfirmationEmail(user: User) {
        userService.createVerificationTokenForUser(user.token!!, user)
        val link = "$hostUrl/api2/auth/registrationConfirm?token=${user.token}"
        val msg = "<p>Please, follow the link to complete your registration:</p><p><a href=\"$link\">$link</a></p>"
        user.email?.let { sendHtmlMessage(user.email!!, "AniFox Security: Registration Confirmation", msg) }
    }

    override fun sendConfirmationPasswordMess(user: User): String{
        return try {
            val msg = "<p>Password confirmation received. Go back to the app to change your password.</p>"
            user.email?.let { sendHtmlMessage(user.email!!, "AniFox Security: Change password confirmation", msg) }
            "Message has been sent"
        } catch (e: Exception){
            "${e.message}"
        }
    }

    override fun sendCompletePasswordChange(user: User): String {
        return try {
            val link = "$hostUrl/api2/auth/confirmPasswordChange?token=${user.token}"
            val msg = "<p>Please, follow the link to change your password:</p><p><a href=\"$link\">$link</a></p>"
            user.email?.let { sendHtmlMessage(user.email!!, "AniFox Security: Change password confirmation", msg) }
            "Message has been sent"
        } catch (e: Exception){
            "${e.message}"
        }
    }

}