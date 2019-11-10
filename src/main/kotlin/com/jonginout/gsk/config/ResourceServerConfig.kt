package com.jonginout.gsk.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler

/**
 * oauth 서버 / 리소스 서버 / 클라이언트
 *
 * 리소스 서버는 어떤 외부 요청이 특정 리소스에 접근을 할 때
 * oauth 서버에 요청을해서 토큰을 가져와서
 * 여기서 인증을 한다.
 *
 * 원래 같으면 oauth 서버는 분리되어 있는게 좀 더 좋다.
 */
@Configuration
@EnableResourceServer
class ResourceServerConfig: ResourceServerConfigurerAdapter() {

    // 리소스 아이디를 정하고 그 외 여러가지 리소스 메타를 수정
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId("event")
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/docs/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/**").permitAll()
//            .antMatchers(HttpMethod.POST, "/api/**").permitAll()
            .anyRequest().authenticated().and()

            .exceptionHandling().accessDeniedHandler(OAuth2AccessDeniedHandler())
    }
}
