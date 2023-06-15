package com.homework.demo.validation;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class RequestValidationException extends HttpClientErrorException {

    public RequestValidationException(String errorMessage) {
        super(HttpStatusCode.valueOf(400), errorMessage);
    }
}
