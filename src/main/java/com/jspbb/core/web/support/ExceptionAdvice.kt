package com.jspbb.core.web.support

import com.jspbb.core.support.Responses
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.shiro.authz.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionAdvice {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthenticatedException::class)
    fun unauthorized(): ResponseEntity<Responses.Body> = Responses.unauthorized()

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthorizedException::class)
    fun forbidden(): ResponseEntity<Responses.Body> = Responses.forbidden()
}