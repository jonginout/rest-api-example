package com.jonginout.gsk.accounts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class AccountSerializer: JsonSerializer<Account>() {

    override fun serialize(account: Account, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeNumberField("id", account.id!!)
        gen.writeStringField("email", account.email!!)
        gen.writeEndObject()
    }

}
