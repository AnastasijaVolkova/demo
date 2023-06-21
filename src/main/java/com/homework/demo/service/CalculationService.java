package com.homework.demo.service;

import com.homework.demo.constants.AppConstants;
import com.homework.demo.request.Bicycle;
import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;
import com.homework.demo.response.Attributes;
import com.homework.demo.response.CalculationObject;
import com.homework.demo.response.PremiumResponse;
import com.homework.demo.response.Risk;
import com.homework.demo.service.support.CalculationScriptSupport;
import com.homework.demo.validation.RequestValidator;
import groovy.lang.GroovyObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final CalculationScriptSupport support;

    public PremiumResponse calculateSumInsuredAndPremiums(SumInsuredAndPremiumsCalculationRequest request) {
        new RequestValidator().validateRequest(request);
        support.validateCalculationScriptsMap(request);

        PremiumResponse response = new PremiumResponse();
        response.initObjects();

        double totalPremium = request.getBicycles().stream()
                .map(this::initCalculationObject)
                .peek(c -> response.getObjects().add(c))
                .map(CalculationObject::getPremium)
                .reduce(0d, Double::sum);
        response.setPremium(BigDecimal.valueOf(totalPremium).setScale(2, RoundingMode.HALF_UP).doubleValue());

        return response;
    }

    private CalculationObject initCalculationObject(Bicycle bicycle) {
        CalculationObject object = new CalculationObject();
        object.initRisks();
        object.setCoverageType(bicycle.getCoverage());
        object.setAttributes(Attributes.init(bicycle));
        object.setSumInsured(bicycle.getSumInsured());

        double premium = bicycle.getRisks().stream()
                .map(r -> createAndCalculateRisk(bicycle, r))
                .peek(r -> object.getRisks().add(r))
                .map(Risk::getPremium)
                .reduce(0d, Double::sum);
        object.setPremium(BigDecimal.valueOf(premium).setScale(2, RoundingMode.HALF_UP).doubleValue());

        return object;
    }

    private Risk createAndCalculateRisk(Bicycle bicycle, String riskType) {
        Risk risk = new Risk(riskType);
        GroovyObject calculationScripts = support.getScriptsByRiskType(riskType);
        risk.setPremium(calculateRiskPremium(riskType, bicycle, calculationScripts));
        risk.setSumInsured(calculateRiskInsuranceSum(riskType, bicycle.getSumInsured(), calculationScripts));
        return risk;
    }

    private double calculateRiskInsuranceSum(String riskType, double sumInsured, GroovyObject calculationScripts) {
        return Optional.ofNullable((Double) calculationScripts.invokeMethod("calculateRiskInsuranceSum", new Object[]{sumInsured}))
                .map(BigDecimal::new)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .orElseThrow(() -> new RuntimeException(String.format("Unable to calculate risk insurance sum for risk type %s", riskType)));
    }

    private double calculateRiskPremium(String riskType, Bicycle bicycle, GroovyObject calculationScripts) {
        int age = Year.now().getValue() - bicycle.getManufactureYear();
        return Optional.ofNullable((Double) calculationScripts.invokeMethod("calculateRiskPremium",
                        new Object[]{riskType, bicycle.getSumInsured(), age, bicycle.getMake(), bicycle.getModel(), bicycle.getRisks().size()}))
                .map(BigDecimal::new)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .orElseThrow(() -> new RuntimeException(String.format("Unable to calculate risk premium for risk type %s", riskType)));
    }

}
