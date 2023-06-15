package com.homework.demo.validation;

import com.homework.demo.request.Bicycle;
import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestValidator {

    private static final String INVALID_YEAR = "Invalid manufacture year for bicycle: %s %s, %d. " +
            "Bicycle must be newer than 10 years.";
    private static final String INVALID_SUM_INSURED = "Invalid sum insured for bicycle: %s %s, %d. " +
            "Sum insured must be less than 10 000.";

    public void validateRequest(SumInsuredAndPremiumsCalculationRequest request) {
        Map<Boolean, List<BicycleValidationMetaData>> invalidBicycles = request.getBicycles().stream()
                .map(BicycleValidationMetaData::of)
                .filter(meta -> meta.hasInvalidYear() || meta.hasInvalidSumInsured())
                .collect(Collectors.partitioningBy(BicycleValidationMetaData::hasInvalidYear));

        if (invalidBicycles.get(true).isEmpty() && invalidBicycles.get(false).isEmpty()) {
            return;
        }
        throw new RequestValidationException(generateErrorMessage(invalidBicycles));
    }

    private String generateErrorMessage(Map<Boolean, List<BicycleValidationMetaData>> invalidBicycles) {
        String invalidYearMessage = invalidBicycles.get(true).stream()
                .map(b -> generateErrorMessageForSingleBicycle(INVALID_YEAR, b.bicycle()))
                .collect(Collectors.joining("\n"));

        return invalidBicycles.get(false).stream()
                .map(b -> generateErrorMessageForSingleBicycle(INVALID_SUM_INSURED, b.bicycle()))
                .collect(Collectors.joining("\n"))
                .concat(invalidYearMessage);
    }

    private String generateErrorMessageForSingleBicycle(String baseMessage, Bicycle bicycle) {
        return baseMessage.formatted(bicycle.getMake(), bicycle.getModel(), bicycle.getManufactureYear());
    }

    private record BicycleValidationMetaData(Bicycle bicycle, boolean invalidYear, boolean invalidSumInsured) {

        static BicycleValidationMetaData of(Bicycle bicycle) {
            int lastValidYear = Year.now().getValue() - 10;
            return new BicycleValidationMetaData(bicycle, bicycle.getManufactureYear() < lastValidYear, bicycle.getSumInsured() >= 10_000);
        }

        public boolean hasInvalidYear() {
            return invalidYear;
        }

        public boolean hasInvalidSumInsured() {
            return invalidSumInsured;
        }
    }
}
