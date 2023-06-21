package businessrules

class RiskPayment_THEFT extends BaseCalculationScript {

    @Override
    String getRiskType() {
        return "THEFT"
    }

    @Override
    Double calculateRiskInsuranceSum(double sumInsured) {
        return sumInsured
    }

    @Override
    Double calculateRiskPremium(String riskType, double sumInsured, int age, String make, String model, int riskCount) {
        def ageFactorData = filterAgeFactorData(make, model, age)

        def riskBasePremium = getRiskBasePremiumData().find { it['RISK_TYPE'] == riskType }['PREMIUM']
        def sumInsuredFactor = calculateSumInsuredFactor(sumInsured, age, ageFactorData)

        return riskBasePremium * sumInsuredFactor
    }

}