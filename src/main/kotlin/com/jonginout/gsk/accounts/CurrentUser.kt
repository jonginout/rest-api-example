package com.jonginout.gsk.accounts

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
annotation class CurrentUser
/**
 * 인증안된 사용자인경우 principal이 그냥 문자열 anonymousUser임
*/
