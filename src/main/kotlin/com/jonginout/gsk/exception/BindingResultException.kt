package com.jonginout.gsk.exception

import org.springframework.validation.BindingResult

class BindingResultException(result: BindingResult) :
    BadRequestException("${result.fieldError?.field} : ${result.fieldError?.defaultMessage}")
