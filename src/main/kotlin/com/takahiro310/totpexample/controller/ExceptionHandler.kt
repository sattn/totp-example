package com.takahiro310.totpexample.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.takahiro310.totpexample.exception.ValidException
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    /**
     * バリデーションエラーハンドラ(BadRequest)
     */
    @ExceptionHandler(ValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidException(request: HttpServletRequest, error: ValidException): ValidErrorResponse {
        logger.debug(error.message, error)
        val globalErrors = mutableListOf<ValidGlobalError>()
        val fieldErrors = mutableListOf<ValidFieldError>()
        error.fieldErrors.forEach { t: FieldError? ->
            fieldErrors.add(ValidFieldError(t!!.field, t.defaultMessage!!))
        }
        error.globalErrors.forEach { t: ObjectError? ->
            globalErrors.add(ValidGlobalError(t!!.defaultMessage!!))
        }
        return ValidErrorResponse(globalErrors, fieldErrors)
    }

    /**
     * その他の例外ハンドラ
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(request: HttpServletRequest, error: Exception): Map<String, String?> {
        logger.debug(error.message, error)
        return mapOf(Pair("message", error.message))
    }
}

data class ValidErrorResponse(
    @JsonProperty("global") val global: List<ValidGlobalError>,
    @JsonProperty("field") val field: List<ValidFieldError>
)

data class ValidGlobalError(
    @JsonProperty("message") val message: String
)

data class ValidFieldError(
    @JsonProperty("name") val field: String,
    @JsonProperty("message") val message: String
)
