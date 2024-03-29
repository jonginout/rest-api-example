package com.jonginout.gsk.accounts

import javax.validation.constraints.NotEmpty

class AccountRequestBody(
    @field:NotEmpty
    var email: String,
    @field:NotEmpty
    var password: String
)
