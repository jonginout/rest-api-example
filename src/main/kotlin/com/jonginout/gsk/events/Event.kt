package com.jonginout.gsk.events

import base.BaseEntity
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.jonginout.gsk.accounts.Account
import com.jonginout.gsk.accounts.AccountSerializer
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "events")
class Event : BaseEntity() {
    var name: String? = null
        private set
    var description: String? = null
        private set
    var location: String? = null
        private set
    var price: Int? = null
        private set
    var startAt: LocalDateTime? = null
        private set
    var endAt: LocalDateTime? = null
        private set
    var offline: Boolean = false
        private set
    @Enumerated(EnumType.STRING)
    var state: EventState = EventState.BEFORE_START
        private set
    @ManyToOne
    @JsonSerialize(using = AccountSerializer::class)
    var creator: Account? = null

    fun fixValue(): Event {
        // 온라인인지 오프라인인지 체크
        this.offline = !this.location.isNullOrBlank()
        this.state = EventState.BEFORE_START
        return this
    }

    fun update(
        name: String?,
        description: String?,
        location: String?,
        price: Int?,
        startAt: LocalDateTime?,
        endAt: LocalDateTime?,
        creator: Account?
    ) {
        this.name = name
        this.description = description
        this.location = location
        this.price = price
        this.startAt = startAt
        this.endAt = endAt
        this.creator = creator
    }

    override fun toString(): String {
        return "Event(name=$name, description=$description, location=$location, price=$price, startAt=$startAt, endAt=$endAt, offline=$offline, state=$state, creator=$creator)"
    }

    companion object {
        fun empty() = Event()

        fun newOf(
            name: String?,
            description: String?,
            location: String?,
            price: Int?,
            startAt: LocalDateTime?,
            endAt: LocalDateTime?,
            creator: Account?
        ): Event {
            return Event().apply {
                this.name = name
                this.description = description
                this.location = location
                this.price = price
                this.startAt = startAt
                this.endAt = endAt
                this.creator = creator
            }
        }
    }

}
