package com.homework.demo.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RequestValidator validator = new RequestValidator();

    @Test
    void validateRequestWithInvalidManufactureYear() {
        SumInsuredAndPremiumsCalculationRequest request = parseJsonToDto("./src/test/resources/RequestWithInvalidManufactureYear.json");

        RequestValidationException ex = assertThrows(RequestValidationException.class,
                () -> validator.validateRequest(request));

        assertEquals(ex.getMessage(), "400 Invalid manufacture year for bicycle: Sensa V2, 2012. " +
                "Bicycle must be newer than 10 years.");
    }

    @Test
    void validateRequestWithInvalidSumInsured() {
        SumInsuredAndPremiumsCalculationRequest request = parseJsonToDto("./src/test/resources/RequestWithInvalidSumInsured.json");

        RequestValidationException ex = assertThrows(RequestValidationException.class,
                () -> validator.validateRequest(request));

        assertEquals(ex.getMessage(), "400 Invalid sum insured for bicycle: OTHER OTHER, 2019. " +
                "Sum insured must be less than 10 000.");
    }

    @Test
    void validateRequestWithInvalidManufactureYearAndSumInsured() {
        SumInsuredAndPremiumsCalculationRequest request = parseJsonToDto("./src/test/resources/RequestWithInvalidManufactureYearAndSumInsured.json");

        RequestValidationException ex = assertThrows(RequestValidationException.class,
                () -> validator.validateRequest(request));

        assertEquals(ex.getMessage(), "400 Invalid sum insured for bicycle: Pearl Gravel SL EVO, 2015. " +
                "Sum insured must be less than 10 000." +
                "Invalid manufacture year for bicycle: Sensa V2, 2010. " +
                "Bicycle must be newer than 10 years.");
    }

    @Test
    void validateRequestWithValidRequest() {
        SumInsuredAndPremiumsCalculationRequest request = parseJsonToDto("./src/test/resources/ValidRequest.json");

        validator.validateRequest(request);
    }

    private SumInsuredAndPremiumsCalculationRequest parseJsonToDto(String path) {
        try {
            return objectMapper.readValue(
                   new File(path),
                   SumInsuredAndPremiumsCalculationRequest.class
           );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}