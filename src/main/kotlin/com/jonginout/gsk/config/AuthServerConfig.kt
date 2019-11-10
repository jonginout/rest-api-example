package com.jonginout.gsk.config

import com.jonginout.gsk.accounts.AccountService
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore

@Configuration
@EnableAuthorizationServer
class AuthServerConfig(
    private val authenticationManager: AuthenticationManager,    // 유저 인증정보를 가지고 있는
    private val passwordEncoder: PasswordEncoder,
    private val accountService: AccountService,
    private val tokenStore: TokenStore
): AuthorizationServerConfigurerAdapter() {

    // 클라이언트의 시크릿도 패스워드 인코딩해서 관리하겠다.
    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security.passwordEncoder(this.passwordEncoder)
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        // 인메모리가 아니라 jdbc에서(디비) 관리하는게 제일 좋은 방법이긴 하다.
        clients.inMemory()
            .withClient("gsk")
            .authorizedGrantTypes("password", "refresh_token")
            .scopes("read", "write")
            .secret(this.passwordEncoder.encode("pass"))
            .accessTokenValiditySeconds(10 * 60)
            .refreshTokenValiditySeconds(1 * 60 * 60)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.authenticationManager(this.authenticationManager)
            .userDetailsService(this.accountService)
            .tokenStore(this.tokenStore)
    }
}

