package com.jonginout.gsk.events

import com.jonginout.gsk.accounts.Account
import com.jonginout.gsk.exception.BadRequestException
import com.jonginout.gsk.exception.NotAuthenticationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class EventService(
    // 생성자가 하나만 있고, 이 생성자로 받아올 파라미터가 이미 빈으로 등록이 되어있다면 Autowired 생략가능
    private val eventRepository: EventRepository,
    private val eventValidator: EventValidator
) {

    fun create(body: EventRequestBody, account: Account): Event {
        this.eventValidator.validate(body)

        val newEvent: Event = Event.newOf(
            name = body.name,
            description = body.description,
            location = body.location,
            price = body.price,
            startAt = body.startAt,
            endAt = body.endAt,
            creator = account
        )
        return this.eventRepository.save(newEvent.fixValue())
    }

    fun list(pageable: Pageable): Page<Event> {
        return this.eventRepository.findAll(pageable)
    }

    fun detail(id: Long): EventResource {
        return EventResource(
            this.eventRepository.findById(id).orElseThrow {
                throw BadRequestException("해당 이벤트가 존재하지 않습니다.")
            }
        )
    }

    fun update(id: Long, body: EventUpdateRequestBody): EventResource {
        this.eventValidator.validateUpdate(body)

        var event = this.eventRepository.findById(id).orElseThrow {
            throw BadRequestException("해당 이벤트가 존재하지 않습니다.")
        }

        event.update(
            body.name,
            body.description,
            body.location,
            body.price,
            body.startAt,
            body.endAt,
            null
        )

        val newEvent = this.eventRepository.save(event)
        return EventResource(newEvent)
    }
}
