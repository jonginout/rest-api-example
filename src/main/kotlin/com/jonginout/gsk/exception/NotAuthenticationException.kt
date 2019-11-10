package com.jonginout.gsk.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
open class NotAuthenticationException(message: String? = "인증이 필요합니다.") : RuntimeException(message)
