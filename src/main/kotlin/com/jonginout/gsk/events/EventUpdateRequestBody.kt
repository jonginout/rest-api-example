package com.jonginout.gsk.events

import java.time.LocalDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class EventUpdateRequestBody (
    @field:NotEmpty
    var name: String?,
    @field:NotEmpty
    var description: String?,
    @field:NotEmpty
    var location: String?,
    @field:Min(0)
    var price: Int?,
    @field:NotNull
    var startAt: LocalDateTime?,
    @field:NotNull
    var endAt: LocalDateTime?
)
