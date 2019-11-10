package com.jonginout.gsk.accounts

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.stream.Collectors

class AccountAdaptor(
    val account: Account
) : User(
    account.email,
    account.password,
    account.role!!.stream().map {
        SimpleGrantedAuthority("ROLE_${it.name}")
    }.collect(Collectors.toSet())
)
