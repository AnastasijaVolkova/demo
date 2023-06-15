package com.homework.demo.response;

import com.homework.demo.enums.RiskType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Risk {

    private RiskType riskType;
    private double sumInsured;
    private double premium;

    public Risk(RiskType riskType) {
        this.riskType = riskType;
    }

}
