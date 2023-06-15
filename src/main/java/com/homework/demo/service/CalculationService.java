package com.homework.demo.service;

import com.homework.demo.enums.RiskType;
import com.homework.demo.request.Bicycle;
import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;
import com.homework.demo.response.Attributes;
import com.homework.demo.response.CalculationObject;
import com.homework.demo.response.PremiumResponse;
import com.homework.demo.response.Risk;
import com.homework.demo.validation.RequestValidator;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final GroovyScriptEngine engine;
    private GroovyObject calculationScripts;

    @PostConstruct
    void initCalculationScripts() throws ScriptException, ResourceException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        calculationScripts = (GroovyObject) engine.loadScriptByName("BaseScript.groovy").getDeclaredConstructor().newInstance();
    }

    public PremiumResponse calculateSumInsuredAndPremiums(SumInsuredAndPremiumsCalculationRequest request) {
        new RequestValidator().validateRequest(request);
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
                .map(r -> initRisk(bicycle, r))
                .peek(r -> object.getRisks().add(r))
                .map(Risk::getPremium)
                .reduce(0d, Double::sum);
        object.setPremium(BigDecimal.valueOf(premium).setScale(2, RoundingMode.HALF_UP).doubleValue());

        return object;
    }

    private Risk initRisk(Bicycle bicycle, RiskType riskType) {
        Risk risk = new Risk(riskType);
        risk.setPremium(calculateRiskPremium(riskType, bicycle));
        risk.setSumInsured(calculateRiskInsuranceSum(riskType, bicycle.getSumInsured()));
        return risk;
    }

    private double calculateRiskInsuranceSum(RiskType riskType, double sumInsured) {
        return Optional.ofNullable((Double) calculationScripts.invokeMethod("calculateRiskInsuranceSum", new Object[]{riskType.name(), sumInsured}))
                .map(BigDecimal::new)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .orElseThrow(() -> new RuntimeException(String.format("Unable to calculate risk insurance sum for risk type %s", riskType)));
    }

    private double calculateRiskPremium(RiskType riskType, Bicycle bicycle) {
        int age = Year.now().getValue() - bicycle.getManufactureYear();
        return Optional.ofNullable((Double) calculationScripts.invokeMethod("calculateRiskPremium",
                        new Object[]{riskType.name(), bicycle.getSumInsured(), age, bicycle.getMake(), bicycle.getModel(), bicycle.getRisks().size()}))
                .map(BigDecimal::new)
                .map(d -> d.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .orElseThrow(() -> new RuntimeException(String.format("Unable to calculate risk premium for risk type %s", riskType)));
    }

}
