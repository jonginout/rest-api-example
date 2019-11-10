package com.jonginout.gsk.config

import com.jonginout.gsk.accounts.AccountRequestBody
import com.jonginout.gsk.base.BaseControllerTest
import org.junit.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthServerConfigTest : BaseControllerTest() {

    @Test
    fun `인증 토근을 발급 받는 테스트`() {
        val data = this.generateAccount()
        val body = data["requestBody"] as AccountRequestBody

        val clientId = "gsk"
        val clientSecret = "pass"

        this.mockMvc.perform(
            post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", body.email)
                .param("password", body.password)
                .param("grant_type", "password")
        )

            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("access_token").exists())
            .andDo(
                MockMvcRestDocumentation.document("oauth/token"
                )
            )
    }
}
