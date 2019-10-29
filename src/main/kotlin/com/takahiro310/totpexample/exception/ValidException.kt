package com.takahiro310.totpexample.exception

import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

class ValidException(val fieldErrors: List<FieldError>, val globalErrors: List<ObjectError>) : RuntimeException()
