package com.jonginout.gsk.base

import com.jonginout.gsk.accounts.AccountRequestBody
import com.jonginout.gsk.config.RestDocsConfiguration
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.security.oauth2.common.util.Jackson2JsonParser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration::class)
@Ignore
class BaseControllerTest : BaseTest() {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    protected fun getAccessToken(): String {
        val data = this.generateAccount()
        val body = data["requestBody"] as AccountRequestBody

        val clientId = "gsk"
        val clientSecret = "pass"

        val perform = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/oauth/token")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(clientId, clientSecret))
                .param("username", body.email)
                .param("password", body.password)
                .param("grant_type", "password")
        )
        var responseBody = perform.andReturn().response.contentAsString
        val parser = Jackson2JsonParser()
        return parser.parseMap(responseBody).get("access_token").toString()
    }
}
