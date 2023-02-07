package io.extremum.functions.api.exception

import io.extremum.sharedmodels.dto.Alert
import io.extremum.sharedmodels.dto.Response
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice(annotations = [RestController::class])
class ExceptionApiHandler {

    @ExceptionHandler(ArgumentValidationException::class)
    fun argumentValidationException(exception: ArgumentValidationException): ResponseEntity<Response> =
        ResponseEntity(
            Response.fail(Alert.errorAlert(exception.message), BAD_REQUEST.value()), BAD_REQUEST
        )

    @ExceptionHandler(IllegalStateException::class)
    fun illegalStateException(exception: IllegalStateException): ResponseEntity<Response> =
        ResponseEntity(
            Response.fail(Alert.errorAlert(exception.message), INTERNAL_SERVER_ERROR.value()), INTERNAL_SERVER_ERROR
        )
}