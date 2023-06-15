package com.homework.demo.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class SumInsuredAndPremiumsCalculationRequest {

    @Valid
    List<Bicycle> bicycles;
}
