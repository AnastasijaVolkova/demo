package businessrules

class RiskPaymentDamageScript extends BaseCalculationScript {

    @Override
    String getRiskType() {
        return "DAMAGE"
    }

    @Override
    Double calculateRiskInsuranceSum(double sumInsured) {
        return sumInsured / 2
    }

    @Override
    Double calculateRiskPremium(String riskType, double sumInsured, int age, String make, String model, int riskCount) {
        def ageFactorData = filterAgeFactorData(make, model, age)

        def riskBasePremium = getRiskBasePremiumData().find { it['RISK_TYPE'] == riskType }['PREMIUM']
        def sumInsuredFactor = calculateSumInsuredFactor(sumInsured, age, ageFactorData)
        def bicycleAgeFactor = calculateBicycleAgeFactor(age, ageFactorData)

        return riskBasePremium * sumInsuredFactor * bicycleAgeFactor

    }

}