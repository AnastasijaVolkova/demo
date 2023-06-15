package com.homework.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.demo.configuration.AppConfig;
import com.homework.demo.enums.Coverage;
import com.homework.demo.enums.RiskType;
import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;
import com.homework.demo.response.Attributes;
import com.homework.demo.response.CalculationObject;
import com.homework.demo.response.PremiumResponse;
import com.homework.demo.response.Risk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import({AppConfig.class, CalculationServiceTestConfiguration.class})
class CalculationServiceTest {

    @Autowired
    private CalculationService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void calculatePremiumsTest() {
        SumInsuredAndPremiumsCalculationRequest request = parseJsonToDto("./src/test/resources/ValidRequest.json");

        PremiumResponse actual = service.calculateSumInsuredAndPremiums(request);

        CalculationObject expectedCalculationObject1 = createExpectedCalculationObject(1000, Coverage.EXTRA, 35.79, a -> {
            a.setMake("Pearl");
            a.setModel("Gravel SL EVO");
            a.setManufactureYear(2015);
        });

        CalculationObject expectedCalculationObject2 = createExpectedCalculationObject(225, Coverage.STANDARD, 6.84, a -> {
            a.setMake("Sensa");
            a.setModel("V2");
            a.setManufactureYear(2020);
        });

        CalculationObject expectedCalculationObject3 = createExpectedCalculationObject(100, Coverage.STANDARD, 24.91, a -> {
            a.setMake("OTHER");
            a.setModel("OTHER");
            a.setManufactureYear(2019);
        });

        assertThat(actual.getPremium()).isEqualTo(67.54);
        assertThat(actual.getObjects()).hasSize(3);
        assertThat(actual.getObjects().get(0)).isEqualToIgnoringGivenFields(expectedCalculationObject1, "risks");
        assertThat(actual.getObjects().get(0).getRisks()).containsExactlyInAnyOrder(
                createRisk(RiskType.THEFT, 1000, 16.88),
                createRisk(RiskType.DAMAGE, 500, 5.41),
                createRisk(RiskType.THIRD_PARTY_DAMAGE, 100, 13.5)
        );
        assertThat(actual.getObjects().get(1)).isEqualToIgnoringGivenFields(expectedCalculationObject2, "risks");
        assertThat(actual.getObjects().get(1).getRisks()).containsExactly(createRisk(RiskType.DAMAGE, 112.5, 6.84));
        assertThat(actual.getObjects().get(2)).isEqualToIgnoringGivenFields(expectedCalculationObject3, "risks");
        assertThat(actual.getObjects().get(2).getRisks()).containsExactlyInAnyOrder(
                createRisk(RiskType.DAMAGE, 50, 9.71),
                createRisk(RiskType.THIRD_PARTY_DAMAGE, 100, 15.2)
        );
    }

    private CalculationObject createExpectedCalculationObject(double sumInsured, Coverage coverage, double premium,
                                                              Consumer<Attributes> attributesData) {
        CalculationObject object = new CalculationObject();
        object.setSumInsured(sumInsured);
        object.setCoverageType(coverage);
        object.setPremium(premium);

        Attributes attributes = new Attributes();
        attributesData.accept(attributes);
        object.setAttributes(attributes);
        return object;
    }

    private Risk createRisk(RiskType riskType, double sumInsured, double premium) {
        Risk risk = new Risk();
        risk.setRiskType(riskType);
        risk.setSumInsured(sumInsured);
        risk.setPremium(premium);
        return risk;
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

@TestConfiguration
@ComponentScan("com.homework.demo.service")
class CalculationServiceTestConfiguration {


}

