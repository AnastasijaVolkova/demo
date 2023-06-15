package com.homework.demo.response;

import com.homework.demo.enums.Coverage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CalculationObject {

    private Attributes attributes;
    private Coverage coverageType;
    private List<Risk> risks;
    private double sumInsured;
    private double premium;

    public void initRisks() {
        this.risks = new ArrayList<>();
    }

}
