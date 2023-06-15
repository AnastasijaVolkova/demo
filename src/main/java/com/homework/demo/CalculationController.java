package com.homework.demo;

import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;
import com.homework.demo.response.PremiumResponse;
import com.homework.demo.service.CalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculate")
@RequiredArgsConstructor
public class CalculationController {

    private final CalculationService service;

    @PostMapping
    public PremiumResponse calculateSumInsuredAndPremiums(@RequestBody @Valid SumInsuredAndPremiumsCalculationRequest request) {
         return service.calculateSumInsuredAndPremiums(request);
    }

}
