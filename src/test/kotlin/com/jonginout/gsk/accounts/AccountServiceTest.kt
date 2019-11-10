package com.jonginout.gsk.accounts

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `findByUsername 성공`() {
        val data = this.generateAccount()
        val body = data["requestBody"] as AccountRequestBody

        val userDetails: UserDetails = this.accountService.loadUserByUsername(body.email)

        println(userDetails.password.toString())
        assertThat(this.passwordEncoder.matches(body.password, userDetails.password)).isTrue()
    }

    private fun generateAccount(index: Int? = null): Map<String, Any> {
        var body = AccountRequestBody(
            "test_account@gmail.com",
            "testpassword111"
        )
        var roles = mutableSetOf<AccountRole>()
        if (index !== null) {
            roles = when (index % 2) {
                0 -> mutableSetOf(
                    AccountRole.USER
                )
                else -> mutableSetOf(
                    AccountRole.USER,
                    AccountRole.ADMIN
                )
            }
            body.apply {
                this.email = "${index}_${this.email}"
                this.password = "${index}_${this.password}"
            }
        }
        return mapOf(
            Pair("account", this.accountService.create(body, roles)),
            Pair("requestBody", body)
        )
    }

}
