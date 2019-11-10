package com.jonginout.gsk.config

import com.jonginout.gsk.accounts.AccountService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun tokenStore(): TokenStore {
        return InMemoryTokenStore()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManager()
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(accountService)
            .passwordEncoder(passwordEncoder)
    }

    // 스프링시큐리티까지는 들어오고 http에서 처리
    /**
     * 이미 oauth2를 통해 인증을 한 친구들만 들어오기 때문에 사실상 무의미?
     */
    override fun configure(http: HttpSecurity) {
        http.cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/docs/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/**").permitAll()
//            .antMatchers(HttpMethod.POST, "/api/**").permitAll()
            .anyRequest().authenticated()
    }
}
