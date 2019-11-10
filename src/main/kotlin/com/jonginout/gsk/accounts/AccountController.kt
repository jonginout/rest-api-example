package com.jonginout.gsk.accounts

import com.jonginout.gsk.exception.BindingResultException
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @PostMapping
    fun create(
        @RequestBody @Valid body: AccountRequestBody,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            throw BindingResultException(bindingResult)
        }
        val newAccount = this.accountService.create(body)

        return ResponseEntity.created(
            linkTo(AccountController::class.java).slash(newAccount.id).toUri()
        ).build()
    }

}
