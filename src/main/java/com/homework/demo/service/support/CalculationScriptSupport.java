package com.homework.demo.service.support;

import com.homework.demo.constants.AppConstants;
import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CalculationScriptSupport {

    private final GroovyScriptEngine engine;
    private Map<String, GroovyObject> calculationScriptsMap;

    @PostConstruct
    void initCalculationScripts() throws ScriptException, ResourceException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
       initMap();
    }

    public void validateCalculationScriptsMap(SumInsuredAndPremiumsCalculationRequest request) {
        if (needReInitScriptMap(request)) {
            try {
                initMap();
            } catch (Exception e) {
                throw new RuntimeException("Failed to re-initialize script map", e);
            }
        }
    }

    public GroovyObject getScriptsByRiskType(String riskType) {
        return Optional.ofNullable(calculationScriptsMap.get(riskType))
                .orElseThrow(() -> new RuntimeException("No scripts have been added for %s risk type".formatted(riskType)));
    }

    private void initMap() throws IOException, ScriptException, ResourceException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("file:" + AppConstants.GROOVY_RULES_PATH + "/RiskPayment*.groovy");
        calculationScriptsMap = new HashMap<>();
        for (Resource resource : resources) {
            GroovyObject groovyObject = (GroovyObject) engine.loadScriptByName(resource.getFilename()).getDeclaredConstructor().newInstance();
            calculationScriptsMap.put((String) groovyObject.invokeMethod("getRiskType", new Object[]{}), groovyObject);
        }
    }

    private boolean needReInitScriptMap(SumInsuredAndPremiumsCalculationRequest request) {
        return request.getBicycles().stream()
                .flatMap(b -> b.getRisks().stream())
                .anyMatch(r -> !calculationScriptsMap.containsKey(r));
    }

}
