package businessrules

abstract class BaseCalculationScript extends BaseScript {

    abstract getRiskType()

    abstract Double calculateRiskInsuranceSum(double sumInsured)

    abstract Double calculateRiskPremium(String riskType, double sumInsured, int age, String make, String model, int riskCount)

    double calculateSumInsuredFactor(double sumInsured, int age, LinkedHashMap<String, Serializable> ageFactorData) {
        def sumInsuredFactorData = getSumInsuredFactorData().find { it['VALUE_FROM'] <= sumInsured && it['VALUE_TO'] >= sumInsured }
        return sumInsuredFactorData['FACTOR_MAX'] - (sumInsuredFactorData['FACTOR_MAX'] - sumInsuredFactorData['FACTOR_MIN'])
                * (ageFactorData['VALUE_TO'] - age) / (ageFactorData['VALUE_TO'] - ageFactorData['VALUE_FROM'])
    }

    double calculateBicycleAgeFactor(int age, LinkedHashMap<String, Serializable> ageFactorData) {
        return ageFactorData['FACTOR_MAX'] - (ageFactorData['FACTOR_MAX'] - ageFactorData['FACTOR_MIN'])
                * (ageFactorData['VALUE_TO'] - age) / (ageFactorData['VALUE_TO'] - ageFactorData['VALUE_FROM'])
    }

    LinkedHashMap<String, Serializable> filterAgeFactorData(String make, String model, int age) {
        return getAgeFactorData().find {it['MAKE'] == make && it['MODEL'] == model && it['VALUE_FROM'] <= age && it['VALUE_TO'] >= age}
                ?: getAgeFactorData().find{it['MODEL'] == null && it['MAKE'] == make && it['VALUE_FROM'] <= age && it['VALUE_TO'] >= age}
                ?: getAgeFactorData().find{it['MODEL'] == null && it['MAKE'] == null && it['VALUE_FROM'] <= age && it['VALUE_TO'] >= age}
    }

}
