package com.jonginout.gsk.accounts

import com.jonginout.gsk.exception.BadRequestException
import com.jonginout.gsk.exception.NotAuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val account = this.accountRepository.findByEmail(username).orElseThrow {
            throw NotAuthenticationException("존재하지 않는 계정입니다.")
        }
        return AccountAdaptor(account)
    }

    fun create(body: AccountRequestBody, roles: MutableSet<AccountRole>? = mutableSetOf(AccountRole.USER)): Account {
        if (!this.accountRepository.findByEmail(body.email).isEmpty) {
            throw BadRequestException("이미 존재하는 이메일 입니다.")
        }

        val newAccount = Account.newOf(
            body.email,
            body.password,
            roles,
            this.passwordEncoder
        )
        return this.accountRepository.save(newAccount)
    }

    private fun authorities(roles: Set<AccountRole>?): MutableCollection<out GrantedAuthority>? {
        return roles!!.stream().map {
            SimpleGrantedAuthority("ROLE_${it.name}")
        }.collect(Collectors.toSet())
    }

}
