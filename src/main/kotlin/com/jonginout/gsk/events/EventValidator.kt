package com.jonginout.gsk.events

import com.jonginout.gsk.exception.BadRequestException
import org.springframework.stereotype.Component

@Component
class EventValidator {

    fun validate(
        body: EventRequestBody
    ) {
        if (body.endAt.isBefore(body.startAt)) {
            throw BadRequestException("이벤트 종료 시점이 시작 시점보다 이전일 수 없습니다.")
        }
    }

    fun validateUpdate(
        body: EventUpdateRequestBody
    ) {
        if (body.endAt!!.isBefore(body.startAt)) {
            throw BadRequestException("이벤트 종료 시점이 시작 시점보다 이전일 수 없습니다.")
        }
    }
}
