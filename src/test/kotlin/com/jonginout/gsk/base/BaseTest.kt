package com.jonginout.gsk.base

import com.fasterxml.jackson.databind.ObjectMapper
import com.jonginout.gsk.accounts.AccountRepository
import com.jonginout.gsk.accounts.AccountRequestBody
import com.jonginout.gsk.accounts.AccountRole
import com.jonginout.gsk.accounts.AccountService
import com.jonginout.gsk.events.Event
import com.jonginout.gsk.events.EventRepository
import com.jonginout.gsk.events.EventRequestBody
import org.junit.Ignore
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("test")
@Ignore
class BaseTest {

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var accountService: AccountService

    @Autowired
    protected lateinit var accountRepository: AccountRepository

    @Autowired
    protected lateinit var eventRepository: EventRepository

    protected fun generateEventRequestBody(): EventRequestBody {
        return EventRequestBody(
            name = "test_event",
            description = "test_event_description",
            location = "seoul",
            price = 0,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(4)
        )
    }

    protected fun generateEvent(index: Int? = null): Event {
        val body = this.generateEventRequestBody()
        if (index !== null) {
            body.apply {
                this.name = "${index}_${this.name}"
                this.description = "${index}_${this.description}"
                this.location = "${index}_${this.location}"
                this.price = index * this.price!!
                this.startAt = this.startAt.minusDays(index.toLong())
                this.endAt = this.endAt.minusDays(index.toLong())
            }
        }
        val event = Event.newOf(
            body.name,
            body.description,
            body.location,
            body.price,
            body.startAt,
            body.endAt,
            null
        )
        return event.fixValue()
    }

    protected fun generateAccount(index: Int? = null): Map<String, Any> {
        var body = AccountRequestBody(
            "test_account@gmail.com",
            "testpassword111"
        )
        var roles = mutableSetOf<AccountRole>()
        if (index !== null) {
            roles = when (index % 2) {
                0 -> mutableSetOf(
                    AccountRole.USER
                )
                else -> mutableSetOf(
                    AccountRole.USER,
                    AccountRole.ADMIN
                )
            }
            body.apply {
                this.email = "${index}_${this.email}"
                this.password = "${index}_${this.password}"
            }
        }
        return mapOf(
            Pair("account", this.accountService.create(body, roles)),
            Pair("requestBody", body)
        )
    }
}
