package com.jonginout.gsk.events

import org.springframework.hateoas.Resource
import org.springframework.hateoas.core.Relation
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.http.HttpMethod

@Relation
class EventResource(
    event: Event
) : Resource<Event>(event) {

    init {
        this.add(
            linkTo(EventController::class.java).slash(event.id).withSelfRel().withType(HttpMethod.GET.name)
        )
        this.add(
            linkTo(EventController::class.java).slash(event.id).withRel("events").withType(HttpMethod.GET.name)
        )
        this.add(
            linkTo(EventController::class.java).slash(event.id).withRel("update").withType(HttpMethod.PUT.name)
        )
    }

}
