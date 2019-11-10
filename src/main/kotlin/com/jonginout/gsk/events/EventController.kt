package com.jonginout.gsk.events

import com.jonginout.gsk.accounts.Account
import com.jonginout.gsk.accounts.CurrentUser
import com.jonginout.gsk.exception.BindingResultException
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService
) {

    @PostMapping
    fun create(
        @RequestBody @Valid body: EventRequestBody,
        bindingResult: BindingResult,
        @CurrentUser account: Account // 스프링시큐리티를 User 바로 주입 받을 수 있다.
    ): ResponseEntity<*> {
        if (bindingResult.hasErrors()) {
            throw BindingResultException(bindingResult)
        }
        val newEvent = this.eventService.create(body, account)

        return ResponseEntity.created(
            linkTo(EventController::class.java).slash(newEvent.id).toUri()
        ).body(EventResource(newEvent))
    }

    @GetMapping
    fun list(
        pageable: Pageable,
        assembler: PagedResourcesAssembler<Event>
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            assembler.toResource(
                this.eventService.list(pageable)
            ) { EventResource(it) }
        )
    }

    @GetMapping("{id}")
    fun detail(
        @PathVariable id: Long
    ): ResponseEntity<*> {
        return ResponseEntity.ok(
            this.eventService.detail(id)
        )
    }

    @PutMapping("{id}")
    fun update(
        @RequestBody @Valid body: EventUpdateRequestBody,
        bindingResult: BindingResult,
        @PathVariable id: Long
    ): ResponseEntity<*> {
        if (bindingResult.hasErrors()) {
            throw BindingResultException(bindingResult)
        }

        return ResponseEntity.ok(
            this.eventService.update(id, body)
        )
    }
}
