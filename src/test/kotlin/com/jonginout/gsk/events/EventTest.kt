package com.jonginout.gsk.events

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

class EventTest {

    private fun generateEvent(): Event {
        val event = Event.newOf(
            name = "test_event",
            description = "test_event_description",
            location = "seoul",
            price = 0,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(4),
            creator = null
        )
        return event.fixValue()
    }

    @Test
    fun newInstance() {
        assertThat(generateEvent()).isNotNull
    }

    @Test
    fun bean() {
        assertThat(generateEvent().name).isEqualTo("test_event")
    }

}
