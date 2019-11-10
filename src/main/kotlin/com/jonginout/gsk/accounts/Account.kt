package com.jonginout.gsk.accounts

import base.BaseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Table

@Entity
@Table(name = "accounts")
class Account : BaseEntity() {
    @Column(unique = true)
    var email: String? = null
        private set
    var password: String? = null
        private set

    @ElementCollection(fetch = FetchType.EAGER)  // 여러개의 이넘을 가질 수 있다. // 데이터 양도 적고 바로바로 필요해서 이거
    @Enumerated(EnumType.STRING)
    var role: MutableSet<AccountRole>? = mutableSetOf(AccountRole.USER)
        private set

    override fun toString(): String {
        return "Account(email=$email, password=$password, role=$role)"
    }

    fun update(
        email: String?,
        password: String?,
        role: MutableSet<AccountRole>?
    ) {
        this.email = email
        this.password = password
        this.role = role
    }

    companion object {
        fun empty() = Account()

        fun newOf(
            email: String?,
            rawPassword: String?,
            role: MutableSet<AccountRole>?,
            passwordEncoder: PasswordEncoder
        ): Account {
            return Account().apply {
                this.email = email
                this.password = passwordEncoder.encode(rawPassword)
                this.role = role
            }
        }
    }
}
