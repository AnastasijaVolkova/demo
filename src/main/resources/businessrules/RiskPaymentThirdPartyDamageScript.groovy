package businessrules

class RiskPaymentThirdPartyDamageScript extends BaseCalculationScript {

    @Override
    String getRiskType() {
        return "THIRD_PARTY_DAMAGE"
    }

    @Override
    Double calculateRiskInsuranceSum(double sumInsured) {
        return 100
    }

    @Override
    Double calculateRiskPremium(String riskType, double sumInsured, int age, String make, String model, int riskCount) {
        def riskCountFactorData = getRiskCountFactorData().find { it['VALUE_FROM'] <= riskCount && it['VALUE_TO'] >= riskCount }
        def ageFactorData = filterAgeFactorData(make, model, age)

        def riskBasePremium = getRiskBasePremiumData().find { it['RISK_TYPE'] == riskType }['PREMIUM']
        def sumInsuredFactor = calculateSumInsuredFactor(sumInsured, age, ageFactorData)

        return riskBasePremium * sumInsuredFactor * riskCountFactorData['FACTOR_MIN']
    }
}