# Demo application for Proof IT

Demo application for Proof IT is a Java WEB application for dealing with sum insured and premium calculations.

## Implementation description

Demo application is Spring Boot WEB application which calculates sum insured and premium using BaseScript.groovy scripts. WEB application takes JSON request such as:

```json
{
  "bicycles" : [
    {
      "make" : "Pearl",
      "model" : "Gravel SL EVO",
      "coverage" : "EXTRA",
      "manufactureYear" : 2015,
      "sumInsured" : 1000,
      "risks" : [
        "THEFT",
        "DAMAGE",
        "THIRD_PARTY_DAMAGE"
      ]
    },
    {
      "make" : "Sensa",
      "model" : "V2",
      "coverage" : "STANDARD",
      "manufactureYear" : 2020,
      "sumInsured" : 225,
      "risks" : [
        "DAMAGE"
      ]
    },
    {
      "make" : "OTHER",
      "model" : "OTHER",
      "coverage" : "STANDARD",
      "manufactureYear" : 2019,
      "sumInsured" : 100,
      "risks" : [
        "DAMAGE",
        "THIRD_PARTY_DAMAGE"
      ]
    }
  ]
}
```
and proceed further with validation part.

### Validation

RequestValidator.class accepts request and validates each sent bicycle to be newer than 10 years and each sum insured to be less than 10000. If validation fails custom exception is thrown (RequestValidationException.class) with message that represents make, model and manufacture year of bicycle which caused validation to fail.

---

After validation is made code proceed with response creation and calculations. For each bicycle sent in request CalculationObject.class is created. In accordance with make, model and manufacture year information Attributes.class is formed with corresponding information. 
Coverage and sum insured information is set in accordance with information in request body as well. Then for each risk type received in request Risk.class is formed and calculations performed to set risk insured sum and premium. 
To perform calculations .groovy scripts are used. For each separate calculation and risk type related method is invoked and required parameters passed to make calculations happen and filter required information for formulas.
Once calculations are made and results set, using .reduce method premiums are being summed up and set to calculation object which represents sum of premiums for all required risks.
Once done additional .reduce method is invoked to calculate premiums for all bicycles and set total premium sum to be paid.
After that response is formed and sent back.

---

### How to add new risk type

To add new risk type 2 calculation (sum insured, premium) rules must be added first as separate script 
with name starting with 'RiskPayment' which extends BaseCalculationScript.groovy and implements required methods. 
Rules must be added in ./src/main/resources/businessrules package. 
Once aforementioned requirements are met new risk type can be passed to API request.